package com.cb.th.claims.cmx.service.surveyor;

import com.cb.th.claims.cmx.entity.fnol.FnolDetail;
import com.cb.th.claims.cmx.entity.surveyor.Surveyor;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorJobStatus;
import com.cb.th.claims.cmx.enums.surveyor.SurveyorStatus;
import com.cb.th.claims.cmx.repository.fnol.FnolDetailsRepository;
import com.cb.th.claims.cmx.repository.surveyor.SurveyorRepository;
import com.cb.th.claims.cmx.repository.surveyor.projection.SurveyorNearestRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyorAssignmentService {

    private final FnolDetailsRepository fnolDetailRepository;
    private final SurveyorRepository surveyorRepository;

    /* =========================================================================
       1) Explicit assignment (kept as-is, but fixes status update semantics)
       ========================================================================= */

    /**
     * Assign a specific surveyor to an FNOL (no proximity logic).
     */
    @Transactional
    public void assignSurveyor(Long fnolId, Long surveyorId) {
        FnolDetail fnol = fnolDetailRepository.findById(fnolId).orElseThrow(() -> new IllegalArgumentException("FNOL not found: " + fnolId));

        Surveyor surveyor = surveyorRepository.findById(surveyorId).orElseThrow(() -> new IllegalArgumentException("Surveyor not found: " + surveyorId));

        Long currentAssignedId = reflectGetLong(fnol, "getAssignedSurveyorId", "getSurveyorId", "getAssignedSurveyorID", "getSurveyorID");

        if (currentAssignedId != null && Objects.equals(currentAssignedId, surveyorId)) {
            log.info("FNOL {} already assigned to surveyor {}. Skipping.", safeRef(fnol), surveyorId);
            return;
        }

        boolean setOk = reflectSetLong(fnol, surveyorId, "setAssignedSurveyorId", "setSurveyorId", "setAssignedSurveyorID", "setSurveyorID");

        if (!setOk) {
            throw new IllegalStateException("Could not set surveyor on FnolDetail. Add a setter like setAssignedSurveyorId(Long) or setSurveyorId(Long).");
        }

        // Update surveyor workload/status
        int before = safeInt(surveyor.getActiveJobsCount());
        int capacity = safeParseCapacity(surveyor.getCapacityPerDay());
        surveyor.setSurveyorJobStatus(SurveyorJobStatus.Accepted);
        surveyor.setActiveJobsCount(before + 1);
        if (capacity > 0 && before + 1 >= capacity) {
            surveyor.setStatus(SurveyorStatus.UNAVAILABLE);
        } else {
            surveyor.setStatus(SurveyorStatus.AVAILABLE);
        }

        fnolDetailRepository.save(fnol);
        surveyorRepository.saveAndFlush(surveyor);

        log.info("Assigned surveyor {} to FNOL {}", surveyorId, safeRef(fnol));
    }

    /* =========================================================================
       2) Nearest assignment (new): uses your native Haversine query + locking
       ========================================================================= */

    /**
     * Auto-assign the nearest AVAILABLE surveyor (capacity-aware) to the given FNOL.
     * Reads accident latitude/longitude from FnolDetail via reflection (works with multiple naming variants).
     *
     * @param fnolId FNOL Detail ID
     * @param take   how many nearest candidates to consider under lock (e.g., 10)
     * @return the Surveyor entity that was assigned
     */
    @Transactional
    public Surveyor autoAssignNearestSurveyor(Long fnolId, int take) {
        FnolDetail fnol = fnolDetailRepository.findById(fnolId).orElseThrow(() -> new IllegalArgumentException("FNOL not found: " + fnolId));

        Double lat = reflectGetDouble(fnol, "getAccidentLat", "getAccidentLatitude", "getLatitude", "getAccident_location_lat", "getAccidentLocationLat");
        Double lng = reflectGetDouble(fnol, "getAccidentLng", "getAccidentLongitude", "getLongitude", "getAccident_location_lng", "getAccidentLocationLng");

        if (lat == null || lng == null) {
            throw new IllegalStateException("Accident location (lat/lng) is missing on FNOL " + safeRef(fnol));
        }

        // Get nearest N candidates
        List<SurveyorNearestRow> nearest = surveyorRepository.findNearestAvailable(lat, lng, Math.max(take, 1));
        if (nearest.isEmpty()) {
            throw new IllegalStateException("No AVAILABLE surveyor found near " + lat + "," + lng + " (capacity-aware).");
        }

        // Try to lock and assign one-by-one in distance order
        for (SurveyorNearestRow row : nearest) {
            Optional<Surveyor> lockedOpt = surveyorRepository.lockById(row.getId());
            if (lockedOpt.isEmpty()) continue;

            Surveyor s = lockedOpt.get();

            // Re-check under lock
            if (s.getStatus() != SurveyorStatus.AVAILABLE) {
                log.debug("Skip surveyor {}: status {}", s.getId(), s.getStatus());
                continue;
            }
            int capacity = safeParseCapacity(s.getCapacityPerDay());
            int active = safeInt(s.getActiveJobsCount());
            if (capacity <= 0 || active >= capacity) {
                log.debug("Skip surveyor {}: capacity {} active {}", s.getId(), capacity, active);
                continue;
            }

            // Set FNOL -> assigned surveyor id
            boolean setOk = reflectSetLong(fnol, s.getId(), "setAssignedSurveyorId", "setSurveyorId", "setAssignedSurveyorID", "setSurveyorID");
            if (!setOk) {
                log.warn("Could not set surveyor on FnolDetail ({}). Trying next candidate.", safeRef(fnol));
                continue; // try next surveyor instead of throwing — keeps flow resilient
            }

            // Update workload/status
            s.setSurveyorJobStatus(SurveyorJobStatus.Pending);
            s.setActiveJobsCount(active + 1);
            if (active + 1 >= capacity) {
                s.setStatus(SurveyorStatus.UNAVAILABLE);
            } else {
                s.setStatus(SurveyorStatus.AVAILABLE);
            }

            // Persist both sides
            fnolDetailRepository.save(fnol);
            surveyorRepository.saveAndFlush(s);

            log.info("Auto-assigned surveyor {} (≈{} km) to FNOL {}", s.getId(), row.getDistanceKm(), safeRef(fnol));
            return s;
        }

        throw new IllegalStateException("No AVAILABLE surveyor could be locked with valid capacity for FNOL " + safeRef(fnol));
    }

    /* =======================================================================
       Helpers – reflection + small coercion utilities
       ======================================================================= */

    private String safeRef(FnolDetail fnol) {
        String val = reflectGetString(fnol, "getFnolReferenceNo", "getReferenceNo", "getFnolRefNo", "getFnolReference", "getRefNo");
        return val != null ? val : String.valueOf(reflectGetLong(fnol, "getId"));
    }

    private static String reflectGetString(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v != null) return String.valueOf(v);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException("Failed invoking " + name + " on " + target.getClass(), e);
            }
        }
        return null;
    }

    private static Long reflectGetLong(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v == null) return null;
                if (v instanceof Number n) return n.longValue();
                return Long.valueOf(String.valueOf(v));
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException("Failed invoking " + name + " on " + target.getClass(), e);
            }
        }
        return null;
    }

    private static Double reflectGetDouble(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v == null) return null;
                if (v instanceof Number n) return n.doubleValue();
                String s = String.valueOf(v).trim();
                if (s.isEmpty()) return null;
                return Double.valueOf(s);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                throw new RuntimeException("Failed invoking " + name + " on " + target.getClass(), e);
            }
        }
        return null;
    }

    private static boolean reflectSetLong(Object target, Long value, String... methodNames) {
        for (String name : methodNames) {
            Optional<Method> method = Arrays.stream(target.getClass().getMethods()).filter(m -> m.getName().equals(name) && m.getParameterCount() == 1).findFirst();
            if (method.isPresent()) {
                Method m = method.get();
                try {
                    Class<?> p = m.getParameterTypes()[0];
                    if (p == long.class) {
                        m.invoke(target, value == null ? 0L : value);
                    } else if (p == Long.class || Number.class.isAssignableFrom(p)) {
                        m.invoke(target, value);
                    } else {
                        m.invoke(target, value == null ? null : p.getConstructor(String.class).newInstance(String.valueOf(value)));
                    }
                    return true;
                } catch (Exception e) {
                    throw new RuntimeException("Failed invoking " + name + " on " + target.getClass(), e);
                }
            }
        }
        return false;
    }

    private static void reflectIncInteger(Object target, String getter, String setter) {
        try {
            Method g = target.getClass().getMethod(getter);
            Method s = target.getClass().getMethod(setter, g.getReturnType());
            Object v = g.invoke(target);
            if (v == null) {
                if (g.getReturnType().equals(int.class)) {
                    s.invoke(target, 1);
                } else {
                    s.invoke(target, 1);
                }
            } else if (v instanceof Number n) {
                int next = n.intValue() + 1;
                if (g.getReturnType().equals(int.class)) {
                    s.invoke(target, next);
                } else {
                    s.invoke(target, Integer.valueOf(next));
                }
            }
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to bump integer counter via " + getter + "/" + setter, e);
        }
    }

    private static int safeParseCapacity(String capacityPerDay) {
        if (capacityPerDay == null || capacityPerDay.isBlank()) return 0;
        try {
            return Integer.parseInt(capacityPerDay.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }
}

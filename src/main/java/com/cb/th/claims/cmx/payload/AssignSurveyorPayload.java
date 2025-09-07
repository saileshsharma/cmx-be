package com.cb.th.claims.cmx.payload;

import com.cb.th.claims.cmx.entity.surveyor.Surveyor;

/**
 * GraphQL payload for assignSurveyor mutation.
 * Matches selection set:
 * id, fnolReferenceNo, status, message, assignedSurveyor { id, name, status, phone }
 */
public class AssignSurveyorPayload {

    private String id;
    private String fnolReferenceNo;
    private String status;              // e.g., "SUCCESS" | "FAILED"
    private String message;
    private SurveyorView assignedSurveyor;

    // Extra context (optional)
    private Long fnolId;
    private Long surveyorId;
    private String surveyorName;
    private Surveyor surveyor;

    public AssignSurveyorPayload() {
    }

    public AssignSurveyorPayload(String id, String fnolReferenceNo, String status, String message, SurveyorView assignedSurveyor) {
        this.id = id;
        this.fnolReferenceNo = fnolReferenceNo;
        this.status = status;
        this.message = message;
        this.assignedSurveyor = assignedSurveyor;
    }

    /* ===== Builder ===== */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AssignSurveyorPayload instance = new AssignSurveyorPayload();

        public Builder id(String id) {
            instance.setId(id);
            return this;
        }

        public Builder fnolReferenceNo(String fnolReferenceNo) {
            instance.setFnolReferenceNo(fnolReferenceNo);
            return this;
        }

        public Builder status(String status) {
            instance.setStatus(status);
            return this;
        }

        public Builder message(String message) {
            instance.setMessage(message);
            return this;
        }

        public Builder assignedSurveyor(SurveyorView assignedSurveyor) {
            instance.setAssignedSurveyor(assignedSurveyor);
            return this;
        }

        public Builder fnolId(Long fnolId) {
            instance.fnolId = fnolId;
            return this;
        }

        public Builder surveyorId(Long surveyorId) {
            instance.surveyorId = surveyorId;
            return this;
        }

        public Builder surveyorName(String surveyorName) {
            instance.surveyorName = surveyorName;
            return this;
        }

        public Builder surveyor(Surveyor surveyor) {
            instance.surveyor = surveyor;
            return this;
        }

        public AssignSurveyorPayload build() {
            return instance;
        }
    }

    /* ===== Getters/Setters ===== */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFnolReferenceNo() {
        return fnolReferenceNo;
    }

    public void setFnolReferenceNo(String fnolReferenceNo) {
        this.fnolReferenceNo = fnolReferenceNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SurveyorView getAssignedSurveyor() {
        return assignedSurveyor;
    }

    public void setAssignedSurveyor(SurveyorView assignedSurveyor) {
        this.assignedSurveyor = assignedSurveyor;
    }

    public Long getFnolId() {
        return fnolId;
    }

    public Long getSurveyorId() {
        return surveyorId;
    }

    public String getSurveyorName() {
        return surveyorName;
    }

    public Surveyor getSurveyor() {
        return surveyor;
    }

    /* ===== Nested View ===== */
    public static class SurveyorView {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String status;
        private String jobStatus;
        private String city;
        private String province;
        private String country;

        public SurveyorView() {
        }

        public SurveyorView(String id, String name, String email, String phone, String status, String jobStatus, String city, String province, String country) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.status = status;
            this.jobStatus = jobStatus;
            this.city = city;
            this.province = province;
            this.country = country;
        }

        /* ===== Builder for SurveyorView ===== */
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final SurveyorView instance = new SurveyorView();

            public Builder id(String id) {
                instance.setId(id);
                return this;
            }

            public Builder name(String name) {
                instance.setName(name);
                return this;
            }

            public Builder email(String email) {
                instance.setEmail(email);
                return this;
            }

            public Builder phone(String phone) {
                instance.setPhone(phone);
                return this;
            }

            public Builder status(String status) {
                instance.setStatus(status);
                return this;
            }

            public Builder jobStatus(String jobStatus) {
                instance.setJobStatus(jobStatus);
                return this;
            }

            public Builder city(String city) {
                instance.setCity(city);
                return this;
            }

            public Builder province(String province) {
                instance.setProvince(province);
                return this;
            }

            public Builder country(String country) {
                instance.setCountry(country);
                return this;
            }

            public SurveyorView build() {
                return instance;
            }
        }

        /* ===== Getters/Setters ===== */
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getJobStatus() {
            return jobStatus;
        }

        public void setJobStatus(String jobStatus) {
            this.jobStatus = jobStatus;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}

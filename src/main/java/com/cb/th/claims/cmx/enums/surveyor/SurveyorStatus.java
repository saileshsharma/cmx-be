package com.cb.th.claims.cmx.enums.surveyor;

public enum SurveyorStatus {

    AVAILABLE, UNAVAILABLE, INACTIVE


    //AVAILABLE,BUSY,OFFLINE,ON-BREAK,INACTIVE


    //THIS SHOULD BE  EITHER AVAILABLE, BUSY, OFFLINE, ON-BREAK, INACTIVE

    // UPDATE GRAPHQL  SCHEMA AND UI

    //AVAILABLE - Surveyor is logged in, location is being tracked, ready to accept a new assignment. At login or after closing last job.
    //BUSY - Surveyor is currently assigned and working on one or more active jobs, not open for dispatch., When a job is accepted until it’s closed.
    //OFFLINE - Surveyor is not logged in, app closed, or unreachable. Before login, after logout, or network lost.
    //ON-BREAK - Surveyor is online but temporarily unavailable for new jobs., Optional for better load balancing.
    //INACTIVE (optional), Surveyor exists in system but account not active for assignment. For HR/admin disabling.



/* this is code  to be uncommented

    AVAILABLE("Surveyor is online and ready for a new assignment"),
    BUSY("Surveyor is currently assigned to an active job"),
    OFFLINE("Surveyor is offline or not connected to the system"),
    ON_BREAK("Surveyor is online but temporarily unavailable for jobs"),
    INACTIVE("Surveyor account is disabled or not active for assignments");

    private final String description;

    SurveyorStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

*/
}
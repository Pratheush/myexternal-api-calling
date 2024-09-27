package org.dailycodebuffer.codebufferspringbootmongodb.work;

public enum Profiles {
    CODEBUFFER("codebuffer"),
    STUDENT("student"),
    TODOMONGO("todo"),;

    private final String profile;

    private Profiles(String profile) {
        this.profile = profile;
    }

    public String profile(){
        return profile;
    }
}

package com.example.newproject;

import java.util.List;

public class Group {
    String groupId;
    String groupName;
    String adminId;
    List<String> membersId;

    public Group() {
    }

    public Group(String groupId, String groupName, String adminId, List<String> membersId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminId = adminId;
        this.membersId = membersId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getMembersId() {
        return membersId;
    }

    public void setMembersId(List<String> membersId) {
        this.membersId = membersId;
    }

}

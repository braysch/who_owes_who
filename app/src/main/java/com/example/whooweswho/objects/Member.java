package com.example.whooweswho.objects;

public class Member
{
    public String member_name;
    public String member_username;
    public String venmoHandle;

    public Member()
    {
        this.member_name = "Default Name";
        this.member_username = "";
        this.venmoHandle = "";
    };

    public Member(String name)
    {
        this.member_name = name;
        this.member_username = "default_username";
    }
}

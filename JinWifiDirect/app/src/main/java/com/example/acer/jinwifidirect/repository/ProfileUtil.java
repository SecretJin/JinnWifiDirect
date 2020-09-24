package com.example.acer.jinwifidirect.repository;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Acer on 1/4/2017.
 */
public class ProfileUtil implements Serializable
{
    private static ProfileUtil profile = null;
    private ArrayList<ProfileUtil> list = new ArrayList<ProfileUtil>();
    private String name,email,description,gender;
    private Context context;
    private ProfileUtil profileUtil;
    public ProfileUtil() {}

    public static ProfileUtil getInstance()
    {
        if(profile == null)
        {
            profile = new ProfileUtil();
        }
        return profile;
    }

    public void initialize(String n,String e,String d,String g)
    {
        name= n;
        email = e;
        description = d;
        gender = g;
    }

    public void setName(String n)
    {
        name = n;
    }

    public void setGender(String g)
    {
        gender = g;
    }

    public void setEmail(String e)
    {
        email = e;
    }

    public void setDescription(String d)
    {
        description = d;
    }

    public String getDescription()
    {
        return description;
    }

    public String getEmail()
    {
        return email;
    }

    public String getName()
    {
        return name;
    }

    public String getGender()
    {
        return gender;
    }

    public void storeProfile(ProfileUtil profile)
    {
        list.add(profile);
    }

    public void getProfile(ProfileUtil profile)
    {
        profileUtil = profile;
    }

    public ArrayList<ProfileUtil> getList()
    {
        return list;
    }

    @Override
    public String toString()
    {
        return String.format(getName());
    }

}

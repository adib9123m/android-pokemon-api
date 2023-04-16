package com.example.myapplication1.models;

public class Pokemon {
    private int nummber;
    private String name;
    private String url;


    public int getNummber() {
        String[] urlPartes=url.split("/");
        return Integer.parseInt(urlPartes[urlPartes.length-1]);
    }

    public void setNummber(int nummber) {
        this.nummber = nummber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

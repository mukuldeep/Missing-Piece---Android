package com.raabnits.missingpiece.models;

public class Users {
    String email,name,pass,phone,ref,pay,upi,referer;
    int life;

    public Users() {
    }

    public Users(String email, String name, String pass, String phone, String ref, String pay, String upi,String referer, int life) {
        this.email = email;
        this.name = name;
        this.pass = pass;
        this.phone = phone;
        this.ref = ref;
        this.pay = pay;
        this.upi = upi;
        this.referer=referer;
        this.life = life;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }
}

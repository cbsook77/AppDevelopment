package com.example.daily_function;

public class FoodInfo {
    String foodname; //음식이름
    String company; //제조사

    public FoodInfo(){

    }
    public FoodInfo(String foodname,String company){
        this.foodname=foodname;
        this.company=company;
    }
    public String getFoodname(){
        return foodname;
    }
    public String getCompany(){
        return company;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }
    public void setCompany(String company) {
        this.company = company;
    }
}

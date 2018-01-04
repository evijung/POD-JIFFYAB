package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

/**
 * Created by musz on 10/11/2016.
 */

public class MyConstant {
    //Explicit
    private int iconAnInt = R.drawable.warning48; // Image for icon 48x48
//    private String link = "http://service.eternity.co.th/";
    private String link = "http://203.154.103.43/";
    private String project = "TmsJiffyAmbient";
    private String imgLink = link + project + "/app/CenterService/";
    private String titleHaveSpaceString = "มีช่องว่าง";
    private String messageHaveSpaceString = "กรุณากรอกข้อมูลให้ครบทุกช่อง!!";
    private String[] columLogin = new String[]{"drv_id", "drv_name", "drv_username", "drv_pic", "gender", "checkGPSIn", "checkGPSOut"};
    private String titleUserFalesString = "ชื่อผู้ใช้ผิดพลาด";
    private String messageUserFalesString = "ไม่มีชื่อผู้ใช้ในฐานข้อมูลของเรา";
    private String titlePasswordFalse = "รหัสผ่านผิดพลาด";
    private String messagePasswordFalse = "กรุณาลองใหม่ คุณใส่รหัสผ่านผิด";
    private String urlUserString = link + project + "/app/CenterService/getUser.php";
    private String urlDataWhereDriverID = link + project + "/app/CenterService/getPlan.php";
    private String urlDataWhereDriverIDanDate = link + project + "/app/CenterService/getPlanDtl.php";
    private String urlDetailWherePlanId = link + project + "/app/CenterService/getTripDtl1.php";
    private String urlContainerList = link + project + "/app/CenterService/getTripDtl_Listview.php";
    private String urlArrivalGPS = link + project + "/app/CenterService/updateArrivalFromDriver.php";
    private String urlSaveImage = link + project + "/app/CenterService/uploadPicture.php";
    private String urlSaveImagePath = link + project + "/app/CenterService/setPicturePath.php";
    private String urlSaveSignPath = link + project + "/app/CenterService/setSignPath.php";
    private String urlUpdateStatus = link + project + "/app/CenterService/updateStatusConfirm.php";
    private String urlUpdateLoad = link + project + "/app/CenterService/updateLoad.php";
    private String urlDriverPicture = link + project + "/app/MasterData/driver/avatar/";
    private String urlDataContainer = link + project + "/app/CenterService/getContainer.php";
    private String urlSaveReturnCont = link + project + "/app/CenterService/setReturnQuantity.php";
    private String urlGetReturnContainerQuantity = link + project + "/app/CenterService/getReturnContainerQuantity.php";
    private String urlGetPlanImg = link + project + "/app/CenterService/getPlanImg.php";

    public String getLink() {
        return link;
    }

    public String getProject() {
        return project;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getUrlGetPlanImg() {
        return urlGetPlanImg;
    }

    public String getUrlGetReturnContainerQuantity() {
        return urlGetReturnContainerQuantity;
    }

    public String getUrlDataContainer() {
        return urlDataContainer;
    }

    public String getUrlSaveReturnCont() {
        return urlSaveReturnCont;
    }

    public String getUrlUpdateLoad() {
        return urlUpdateLoad;
    }

    public String getUrlDriverPicture() {
        return urlDriverPicture;
    }

    public String getUrlUpdateStatus() {
        return urlUpdateStatus;
    }

    public String getUrlSaveSignPath() {
        return urlSaveSignPath;
    }

    public String getUrlSaveImagePath() {
        return urlSaveImagePath;
    }

    public String getUrlSaveImage() {
        return urlSaveImage;
    }

    public String getUrlArrivalGPS() {
        return urlArrivalGPS;
    }

    public String getUrlContainerList() {
        return urlContainerList;
    }

    public String getUrlDetailWherePlanId() {
        return urlDetailWherePlanId;
    }

    public String getUrlDataWhereDriverIDanDate() {
        return urlDataWhereDriverIDanDate;
    }

    public String getUrlDataWhereDriverID() {
        return urlDataWhereDriverID;
    }

    public String getTitlePasswordFalse() {
        return titlePasswordFalse;
    }

    public String getMessagePasswordFalse() {
        return messagePasswordFalse;
    }

    public String getTitleUserFalesString() {
        return titleUserFalesString;
    }

    public String getMessageUserFalesString() {
        return messageUserFalesString;
    }

    public String[] getColumLogin() {
        return columLogin;
    }

    public String getUrlUserString() {
        return urlUserString;
    }

    public int getIconAnInt() {
        return iconAnInt;
    }

    public String getTitleHaveSpaceString() {
        return titleHaveSpaceString;
    }

    public String getMessageHaveSpaceString() {
        return messageHaveSpaceString;
    }
}//Main Class
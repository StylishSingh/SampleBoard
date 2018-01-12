package com.imagepicker.utils;

/**
 * auther Anuj Sharma on 9/21/2017.
 */

public interface Constants {
    String SELECTED_MEDIA_LIST_OBJ = "selected_media_list_obj";

    String SELECTED_MEDIA_PATH = "selected_media_path";

    String SelectedMediaObj = "selected_media_obj";



    /*
   API BASE URL
    */
//    String DEV_APIURL = "http://mapofmissionapi.azurewebsites.net/";
    String DEV_APIURL = "http://10.20.1.17:8092/";
    String LIVE_APIURL = "http://mapofmissionapi.azurewebsites.net/";

    String API_URL = DEV_APIURL;

    String DEV_WEB_BASE_URL = "http://10.20.1.17:8095/";
    String LIVE_WEB_BASE_URL = "http://mapofmissions.azurewebsites.net/";


    String LOGIN_API = "token";

    String GET_PROJECTLIST_API = "api/project/Projects";
    String GET_NOTIFICATION_API = "api/project/GetAllNotifications";

}

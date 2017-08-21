package com.csymicc.poster;

/**
 * Created by Micc on 7/31/2017.
 */

public interface MsgType {
    int PM_REGISTRATION = 0;
    int PM_REGISTRATION_SUCCEED = 0;
    int PM_REGISTRATION_FAIL = 1;
    int PM_SIGNOUT = 2;
    int PM_SIGNIN_1 = 3;
    int PM_SIGNIN_2 = 4;
    int PM_SIGNIN_SUCCEED = 5;
    int PM_SIGNIN_FAIL = 6;
    int PM_ENTER_MAIN = 15;
    int PM_SEARCH = 70;
    int PM_SEARCH_RESPONSE = 80;
    int PM_FOLLOW = 7;
    int PM_FOLLOW_CONFIRM = 77;
    int PM_UNFOLLOW = 8;
    int PM_POST = 9;
    int PM_POST_CONFIRM = 90;
    int PM_REQUEST = 10;
    int PM_REQUEST_END = 11;
    int CHANGE_BUTTON = 12;
    int RECOVER_BUTTON = 13;
    int TIME_OUT = 14;
    int START_SIGN = 15;
    int SEND_FOLLOW = 16;
}

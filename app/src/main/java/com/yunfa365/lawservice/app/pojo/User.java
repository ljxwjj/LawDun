package com.yunfa365.lawservice.app.pojo;

import android.content.Context;

import com.yunfa365.lawservice.app.pojo.event.UserRoleEvent;
import com.yunfa365.lawservice.app.utils.SpUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class User {
    public int ID;
    public int LawId;         // 律所ID
    public String Mobile;     //
    public String FullName;   //
    public int Sexs;          // 性别
    public String Phone;      // 电话号码
    public String UNums;      // 身份证号
    public String UserCode;   // 执业证号
    public int ProvinceId;
    public int CityId;
    public int AreaId;
    public int GroupsId;      // 权限组
    public int IsActive;      // 是否在职 离职后禁止通过页面修改成在职
    public int Stat;          // 审核状态
    public String Addtime;    // 入库时间
    public String app_key;

    // 我的基本资料
    public String weixin_key;
    public String WOpenID;
    public int WGuanZhu;
    public String TongJi_Case;
    public String TongJi_Off;
    public String TongJi_Bill;
    public String LawIdTxt;
    public String SexsTxt;
    public String ProvinceIdTxt;
    public String CityIdTxt;
    public String AreaIdTxt;
    public String GroupsIdTxt;
    public String IsActiveTxt;
    public String StatTxt;

    public transient UserRole mRole;
    private transient boolean onLoadUserRole = false; // 角色权限加载状态

    public void loadUserRole(Context context) {
        if (mRole != null && onLoadUserRole) {
            return;
        }
        UserRole roles = new UserRole();
        roles.Power2 = "";
        roles.Power3 = "61,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20";
        AppGlobal.mUser.mRole = roles;
        AppGlobal.mUser.mRole.parseRoles();
        EventBus.getDefault().post(new UserRoleEvent(200));

        /*if (mRole != null && onLoadUserRole) {
            return;
        }
        onLoadUserRole = true;

        AppRequest request = new AppRequest.Build("Home/NewGetUserPowerList")
                .addParam("Uid", AppGlobal.mUser.WUid + "")
                .create();

        new HttpJsonFuture.Builder(context)
                .setData(request)
                .setListener(new AgnettyFutureListener() {
                    @Override
                    public void onComplete(AgnettyResult result) {
                        AppResponse resp = (AppResponse) result.getAttach();
                        if (resp != null && resp.flag) {
                            UserRole roles = resp.resultsToObject(UserRole.class);
                            AppGlobal.mUser.mRole = roles;
                            AppGlobal.mUser.mRole.parseRoles();
                            EventBus.getDefault().post(new UserRoleEvent(200));
                        } else {
                            EventBus.getDefault().post(new UserRoleEvent(-1));
                        }
                        onLoadUserRole = false;
                    }

                    @Override
                    public void onException(AgnettyResult result) {
                        onLoadUserRole = false;
                        EventBus.getDefault().post(new UserRoleEvent(-1));
                    }
                })
                .execute();*/

    }

    public void save(Context context) {
        String str = StringUtil.objectToJson(this);
        SpUtil.setCurrentUser(context, str);
    }


    public final boolean isOnLoadUserRole() {
        return onLoadUserRole;
    }
}

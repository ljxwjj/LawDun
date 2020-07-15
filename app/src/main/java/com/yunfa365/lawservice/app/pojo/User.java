package com.yunfa365.lawservice.app.pojo;

import android.content.Context;

import com.android.agnetty.core.AgnettyFutureListener;
import com.android.agnetty.core.AgnettyResult;
import com.yunfa365.lawservice.app.constant.AppCst;
import com.yunfa365.lawservice.app.future.HttpJsonFuture;
import com.yunfa365.lawservice.app.pojo.event.UserRoleEvent;
import com.yunfa365.lawservice.app.pojo.http.AppRequest;
import com.yunfa365.lawservice.app.pojo.http.AppResponse;
import com.yunfa365.lawservice.app.utils.SpUtil;
import com.yunfa365.lawservice.app.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class User {
    public int Wid;         //    律所ID
    public int WUid;        //  律师编号
    public String Mobile;   // 手机号码
    public String FullName; //  律师名称
    public int Stat;        //   用户状态  0禁用 1启动         个人版用户 -1 未认证  0待审核  1认证已通过   2认证未通过
    public String LawyerName;// 律所名称
    public String SignKey;  // 动态密钥
    public int Attestation; // 认证状态  0未认证  1认证通过

    public transient UserRole mRole;
    private transient boolean onLoadUserRole = false; // 角色权限加载状态

    public void loadUserRole(Context context) {
        if (mRole != null && onLoadUserRole) {
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
                .execute();

    }

    public void save(Context context) {
        String str = StringUtil.objectToJson(this);
        SpUtil.setCurrentUser(context, str);
    }


    public final boolean isOnLoadUserRole() {
        return onLoadUserRole;
    }
}

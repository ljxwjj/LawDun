package com.yunfa365.lawservice.app.constant;

import com.yunfa365.lawservice.app.pojo.base.BaseBean;

public class BaseCst {
	public static final int ADD_REQUEST_CODE = 1;
	public static final int EDIT_REQUEST_CODE = 2;
	public static final int DETAIL_REQUEST_CODE = 3;
	public static final int DELETE_REQUEST_CODE = 4;

	public static final int RESULT_DELETE = 101;
	public static final int RESULT_EDITED = 102;
	// 操作成功
	public static final int HTTP_CODE_SUCCESS = 0;

	public static BaseBean[] sffss = {new BaseBean(801, "免费")
			, new BaseBean(802, "计件收费")
			, new BaseBean(803, "计时收费")
			, new BaseBean(804, "按标的额比例收费")
			, new BaseBean(805, "风险代理收费")};
}





package com.xapp.jjh.base_ijk.inter;

/**
 * Created by Taurus on 2016/8/30.
 */
public interface OnErrorListener {

    int ERROR_CODE_COMMON = 30001;

    void onError(int errorCode);
}

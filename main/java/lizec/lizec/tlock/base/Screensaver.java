package lizec.lizec.tlock.base;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Screensaver {

    private static final int MSG_TIMEOUT = 0x0;

    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int DEFAULT_TIMEOUT = MINUTE; //默认时间为1分钟

    private OnTimeOutListener mOnTimeOutListener;
    private int mScreensaverTimeout = DEFAULT_TIMEOUT;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_TIMEOUT){

                //调用回调通知接收者
                if(mOnTimeOutListener != null){
                    Log.i("", "handleMessage: 到达时间，回调");
                    mOnTimeOutListener.onTimeOut(Screensaver.this);
                }
            }
        }
    };


    public Screensaver(int screensaverTiemout) {
        mScreensaverTimeout = screensaverTiemout;
    }

    public Screensaver() {
    }


    /**
     * 开始计时
     */
    public void start() {
        Message message = mHandler.obtainMessage(MSG_TIMEOUT); //包装消息
        mHandler.sendMessageDelayed(message, mScreensaverTimeout); //延时发送消息
    }

    /**
     * 停止计时
     */
    public void stop() {
        mHandler.removeMessages(MSG_TIMEOUT); //移除消息
    }

    /**
     * 重置时间
     */
    public void resetTime() {
        stop();
        start();
    }

    public void setScreensaverTiemout(int mScreensaverTiemout) {
        this.mScreensaverTimeout = mScreensaverTiemout;
    }

    public int getScreensaverTiemout() {
        return mScreensaverTimeout;
    }

    public void setOnTimeOutListener(OnTimeOutListener onTimeOutListener) {
        this.mOnTimeOutListener = onTimeOutListener;
    }

    /**
     * 屏幕保护时间到监听
     */
    public interface OnTimeOutListener {
        void onTimeOut(Screensaver screensaver);
    }

}

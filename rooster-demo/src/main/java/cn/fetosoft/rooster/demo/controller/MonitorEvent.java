package cn.fetosoft.rooster.demo.controller;

import org.springframework.context.ApplicationEvent;

/**
 * 监控事件
 * @author guobingbing
 * @wechat t_gbinb
 * @since 2021/8/13 20:24
 */
public class MonitorEvent extends ApplicationEvent {

    private String fireTime;
    private long runTime;

    /**
     *
     */
    public MonitorEvent(String code) {
        super(code);
    }

    public String getCode() {
        return getSource().toString();
    }

    public String getFireTime() {
        return fireTime;
    }

    public void setFireTime(String fireTime) {
        this.fireTime = fireTime;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }
}

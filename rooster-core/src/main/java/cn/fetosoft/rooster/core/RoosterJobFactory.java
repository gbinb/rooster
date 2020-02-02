package cn.fetosoft.rooster.core;

import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * @author guobingbing
 * @create 2020/1/31 9:52
 */
@Component("roosterJobFactory")
public class RoosterJobFactory extends SpringBeanJobFactory {
}

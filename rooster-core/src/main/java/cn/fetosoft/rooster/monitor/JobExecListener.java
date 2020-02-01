package cn.fetosoft.rooster.monitor;

/**
 * Job monitoring
 * @author guobingbing
 * @create 2020/2/1 9:42
 */
public interface JobExecListener {


	void beforeExec(JobContext jobContext);


	void afterExec(JobContext jobContext);
}

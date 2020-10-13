package org.moose.operator.configure;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.moose.operator.job.MySimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * @author taohua
 */
//@Configuration
public class MyJobConfiguration {
  private final String cron = "0/5 * * * * ?";
  private final int shardingTotalCount = 1;
  private final String shardingItemParameters = "0=A,1=B,2=C";
  private final String jobParameters = "parameter";

  @Autowired
  private ZookeeperRegistryCenter regCenter;

  @Bean
  public SimpleJob stockJob() {
    return new MySimpleJob();
  }

  @Bean(initMethod = "init")
  public JobScheduler simpleJobScheduler(final SimpleJob simpleJob) {
    return new SpringJobScheduler(simpleJob, regCenter,
        createLiteJobConfiguration(simpleJob.getClass(),
            cron, shardingTotalCount, shardingItemParameters, jobParameters));
  }

  private LiteJobConfiguration createLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
      final String cron,
      final int shardingTotalCount,
      final String shardingItemParameters,
      final String jobParameters) {
    // 定义作业核心配置
    JobCoreConfiguration simpleCoreConfig =
        JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount).
            shardingItemParameters(shardingItemParameters).jobParameter(jobParameters).build();
    // 定义SIMPLE类型配置
    SimpleJobConfiguration simpleJobConfig =
        new SimpleJobConfiguration(simpleCoreConfig, jobClass.getCanonicalName());
    // 定义Lite作业根配置
    return LiteJobConfiguration.newBuilder(simpleJobConfig).overwrite(true).build();
  }
}

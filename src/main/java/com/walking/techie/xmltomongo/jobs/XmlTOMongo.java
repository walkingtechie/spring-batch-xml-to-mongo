package com.walking.techie.xmltomongo.jobs;

import com.walking.techie.xmltomongo.converter.ReportConverter;
import com.walking.techie.xmltomongo.model.Report;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

@Configuration
@EnableBatchProcessing
public class XmlTOMongo {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;
  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Autowired
  private ReportConverter reportConverter;
  @Autowired
  private MongoTemplate mongoTemplate;

  @Bean
  public Job reportJob() {
    return jobBuilderFactory.get("reportJob").incrementer(new RunIdIncrementer()).flow(step1())
        .end().build();
  }

  @Bean
  public Step step1() {
    return stepBuilderFactory.get("step1").<Report, Report>chunk(10).reader(reader())
        .writer(writer()).build();
  }

  @Bean
  public StaxEventItemReader<Report> reader() {
    StaxEventItemReader<Report> reader = new StaxEventItemReader<>();
    reader.setResource(new ClassPathResource("report.xml"));
    reader.setFragmentRootElementName("record");
    reader.setUnmarshaller(unmarshaller());
    return reader;
  }

  @Bean
  public XStreamMarshaller unmarshaller() {
    XStreamMarshaller unmarshal = new XStreamMarshaller();

    Map<String, Class> aliases = new HashMap<String, Class>();
    aliases.put("record", Report.class);
    unmarshal.setAliases(aliases);
    unmarshal.setConverters(reportConverter);
    return unmarshal;
  }

  @Bean
  public MongoItemWriter<Report> writer() {
    MongoItemWriter<Report> writer = new MongoItemWriter<>();
    writer.setTemplate(mongoTemplate);
    writer.setCollection("report");
    return writer;
  }
}

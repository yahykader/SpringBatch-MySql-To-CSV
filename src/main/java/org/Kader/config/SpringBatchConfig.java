package org.Kader.config;

import javax.sql.DataSource;

import org.Kader.batch.UserItemProcessor;
import org.Kader.entities.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	@Autowired
	public DataSource dataSource;
	
	
	@Qualifier(value="importUserJob")
	@Bean
	public Job importUserJob() throws Exception {
		return jobBuilderFactory.get("importUserJob")
				                .incrementer(new RunIdIncrementer())
				               // .listener(listener())
				                .start(step1())
				                .build();
	}
	
	@Bean
	public Step step1() throws Exception{
		return stepBuilderFactory.get("step1")
								 .<User,User>chunk(5)
								 .reader(reader())
								 .processor(processor())
								 .writer(writer())
								 .build();	
	}
	
	@Bean
	public JdbcCursorItemReader<User> reader(){
		JdbcCursorItemReader<User> reader=new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT  * FROM user");
		reader.setRowMapper(new UserRowMapper());
		return reader;	
	}
	
	@Bean
	public UserItemProcessor processor() {
		return new UserItemProcessor();
	}

	/*@Bean
	@StepScope
	Resource inputFileRessource(@Value("#{jobParameters[fileName]}") final String fileName)  throws Exception{
		return  new ClassPathResource(fileName);
	}
	*/

	@Bean
	//@StepScope
	public FlatFileItemWriter<User> writer() throws Exception{
		FlatFileItemWriter<User> writer=new FlatFileItemWriter<User>();
		writer.setResource(new ClassPathResource("users.csv"));
		writer.setLineAggregator(new DelimitedLineAggregator<User>() {{
			setDelimiter(",");
			setFieldExtractor(new BeanWrapperFieldExtractor<User>() {{
				setNames(new String[] {"id","firstName","lastName","email","age"});
			}});
		}});
		return writer;
	}

	/*private LineMapper<User> lineMapper() {
		DefaultLineMapper<User> lineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer =new DelimitedLineTokenizer();
		
		
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setNames(new String[] {"name"});
		lineTokenizer.setStrict(false);
		
		BeanWrapperFieldSetMapper<User> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(User.class);
		
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		
		return lineMapper;
	}
	*/
	
	
	
	/*@Bean
	public UserItemWriter writer() {
		return new UserItemWriter();
	}
*/
	
	/*@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource db=new DriverManagerDataSource();
		db.setDriverClassName("com.mysql.cj.jdbc.Driver");
		db.setUrl("jdbc:mysql://localhost/loadcsv");
		db.setUsername("root");
		db.setPassword("root");
		return dataSource;
	}
	*/
}

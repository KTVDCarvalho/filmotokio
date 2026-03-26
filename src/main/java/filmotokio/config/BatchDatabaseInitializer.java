package filmotokio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

// Spring Batch database initialization
// Creates necessary tables for Spring Batch
@Configuration
public class BatchDatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(BatchDatabaseInitializer.class);

    // Database connection for executing SQL statements
    @Autowired
    private DataSource dataSource;

    // Runs after bean creation
    // Initializes Spring Batch tables
    @PostConstruct
    public void initializeBatchTables() {
        try {
            // Log that we are starting to create the tables
            logger.info("[BatchDatabaseInitializer] - Creating Spring Batch tables");
            
            // Create a template for executing SQL statements
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            // Call the method that creates all the tables
            createBatchTables(jdbcTemplate);
            
            // Log that the tables were created successfully
            logger.info("[BatchDatabaseInitializer] - Spring Batch tables created successfully");
            
        } catch (Exception e) {
            // Log any errors that occur during table creation
            logger.error("[BatchDatabaseInitializer] - Error creating batch tables: {}", e.getMessage(), e);
        }
    }

    // Creates Spring Batch tables
    private void createBatchTables(JdbcTemplate jdbcTemplate) {
        // Drop tables if they exist to avoid conflicts
        try {
            // Drop all existing batch tables
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_STEP_EXECUTION");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_PARAMS");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_JOB_EXECUTION");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_JOB_INSTANCE");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_SEQ");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_SEQ");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_JOB_SEQ");
            jdbcTemplate.execute("DROP TABLE IF EXISTS BATCH_STEP_SEQ");
        } catch (Exception e) {
            // Log debug message if tables don't exist yet
            logger.debug("[BatchDatabaseInitializer] - Tables may not exist yet: {}", e.getMessage());
        }

        // Create tables using InnoDB engine
        // Create job instance table to store job information
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_JOB_INSTANCE (
                JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
                VERSION BIGINT,
                JOB_NAME VARCHAR(100) NOT NULL,
                JOB_KEY VARCHAR(32) NOT NULL,
                constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
            )
        """);

        // Create job execution table to store job run information
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_JOB_EXECUTION (
                JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                VERSION BIGINT,
                JOB_INSTANCE_ID BIGINT NOT NULL,
                CREATE_TIME DATETIME NOT NULL,
                START_TIME DATETIME,
                END_TIME DATETIME,
                STATUS VARCHAR(10),
                EXIT_CODE VARCHAR(20),
                EXIT_MESSAGE VARCHAR(2500),
                LAST_UPDATED DATETIME,
                JOB_CONFIGURATION_LOCATION VARCHAR(2500),
                constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID) references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
            )
        """);

        // Create job execution parameters table to store job parameters
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
                JOB_EXECUTION_ID BIGINT NOT NULL,
                TYPE_CD VARCHAR(6) NOT NULL,
                KEY_NAME VARCHAR(100) NOT NULL,
                STRING_VAL VARCHAR(250),
                DATE_VAL DATETIME,
                LONG_VAL BIGINT,
                DOUBLE_VAL DOUBLE,
                IDENTIFYING CHAR(1) NOT NULL,
                constraint JBP_EXECUTION_FK foreign key (JOB_EXECUTION_ID) references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID),
                constraint JBP_JOB_EXEC_PARAMS_PK primary key (JOB_EXECUTION_ID, KEY_NAME)
            )
        """);

        // Create job execution context table to store job context data
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
                JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                SHORT_CONTEXT VARCHAR(2500),
                SERIALIZED_CONTEXT TEXT,
                constraint JEC_EXECUTION_FK foreign key (JOB_EXECUTION_ID) references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
            )
        """);

        // Create step execution table to store step run information
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_STEP_EXECUTION (
                STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                VERSION BIGINT NOT NULL,
                STEP_NAME VARCHAR(100) NOT NULL,
                JOB_EXECUTION_ID BIGINT NOT NULL,
                START_TIME DATETIME,
                END_TIME DATETIME,
                STATUS VARCHAR(10),
                COMMIT_COUNT BIGINT,
                READ_COUNT BIGINT,
                FILTER_COUNT BIGINT,
                WRITE_COUNT BIGINT,
                READ_SKIP_COUNT BIGINT,
                WRITE_SKIP_COUNT BIGINT,
                PROCESS_SKIP_COUNT BIGINT,
                ROLLBACK_COUNT BIGINT,
                EXIT_CODE VARCHAR(20),
                EXIT_MESSAGE VARCHAR(2500),
                LAST_UPDATED DATETIME,
                constraint STEP_EXEC_JOB_EXEC_FK foreign key (JOB_EXECUTION_ID) references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
            )
        """);

        // Create step execution context table to store step context data
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
                STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                SHORT_CONTEXT VARCHAR(2500),
                SERIALIZED_CONTEXT TEXT,
                constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID) references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
            )
        """);

        // Create sequence tables for generating IDs
        jdbcTemplate.execute("""
            CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
                ID BIGINT NOT NULL PRIMARY KEY,
                ORDER_VAL BIGINT,
                unique (ID)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
                ID BIGINT NOT NULL PRIMARY KEY,
                ORDER_VAL BIGINT,
                unique (ID)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE BATCH_JOB_SEQ (
                ID BIGINT NOT NULL PRIMARY KEY,
                ORDER_VAL BIGINT,
                unique (ID)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE BATCH_STEP_SEQ (
                ID BIGINT NOT NULL PRIMARY KEY,
                ORDER_VAL BIGINT,
                unique (ID)
            )
        """);

        // Insert initial sequence values
        jdbcTemplate.update("INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, ORDER_VAL) values (0, 0)");
        jdbcTemplate.update("INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, ORDER_VAL) values (0, 0)");
        jdbcTemplate.update("INSERT INTO BATCH_JOB_SEQ (ID, ORDER_VAL) values (0, 0)");
        jdbcTemplate.update("INSERT INTO BATCH_STEP_SEQ (ID, ORDER_VAL) values (0, 0)");
    }
}

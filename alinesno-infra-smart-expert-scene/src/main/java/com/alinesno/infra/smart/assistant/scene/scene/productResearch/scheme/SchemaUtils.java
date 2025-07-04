//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.scheme;
//
//import lombok.Data;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.core.namedparam.SqlParameterSource;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//
//import javax.sql.DataSource;
//import java.sql.PreparedStatement;
//import java.util.List;
//import java.util.Objects;
//
///**
// * Schema工具类，用于管理租户Schema和操作相关表
// */
//public class SchemaUtils {
//
//    private final JdbcTemplate jdbcTemplate;
//    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    // 需要在公共空间下创建空表
//    private static final String CREATE_PUBLIC_SPACE_SQL = """
//            -- 1. 先创建序列
//            CREATE SEQUENCE IF NOT EXISTS public.smart_git_commit_message_id_seq;
//            CREATE SEQUENCE IF NOT EXISTS public.team_members_id_seq;
//            CREATE SEQUENCE IF NOT EXISTS public.project_management_id_seq;
//
//            -- 2. 然后创建表（使用上面修改后的SQL）
//
//            -- 创建git_commit_message表的SQL（带注释）
//            CREATE TABLE IF NOT EXISTS public.git_commit_message (
//                "id" bigint NOT NULL DEFAULT nextval('public.smart_git_commit_message_id_seq'::regclass),
//                "create_time" timestamp,
//                "git_file_path" varchar(255),
//                "commit_message" text,
//                "commit_diff" text,
//                "role" varchar(255),
//                "member_id" bigint,
//                "project_id" bigint,
//                CONSTRAINT "pk_public_smart_git_commit_message" PRIMARY KEY ("id")
//            );
//            COMMENT ON TABLE public.git_commit_message IS 'Git提交信息记录表';
//            COMMENT ON COLUMN public.git_commit_message.id IS '记录唯一标识ID';
//            COMMENT ON COLUMN public.git_commit_message.create_time IS '记录创建时间';
//            COMMENT ON COLUMN public.git_commit_message.git_file_path IS 'Git文件路径';
//            COMMENT ON COLUMN public.git_commit_message.commit_message IS '提交详细描述信息';
//            COMMENT ON COLUMN public.git_commit_message.commit_diff IS 'Git代码差异描述';
//            COMMENT ON COLUMN public.git_commit_message.role IS '提交者角色';
//            COMMENT ON COLUMN public.git_commit_message.member_id IS '提交组织成员ID';
//            COMMENT ON COLUMN public.git_commit_message.project_id IS '所属项目ID';
//
//            -- 创建team_members表的SQL（带注释）
//            CREATE TABLE IF NOT EXISTS public.team_members (
//                "id" bigint NOT NULL DEFAULT nextval('public.team_members_id_seq'::regclass),
//                "chinese_name" varchar(255) NOT NULL,
//                "email" varchar(255),
//                "position" varchar(255),
//                "responsibility" text,
//                CONSTRAINT "pk_public_team_members" PRIMARY KEY ("id")
//            );
//            COMMENT ON TABLE public.team_members IS '团队成员表';
//            COMMENT ON COLUMN public.team_members.id IS '组织成员ID';
//            COMMENT ON COLUMN public.team_members.chinese_name IS '成员中文名';
//            COMMENT ON COLUMN public.team_members.email IS '成员邮箱地址';
//            COMMENT ON COLUMN public.team_members.position IS '成员岗位名称';
//            COMMENT ON COLUMN public.team_members.responsibility IS '成员主要工作职责描述';
//
//            -- 创建project_management表的SQL（简化版，只包含id和name）
//            CREATE TABLE IF NOT EXISTS public.project_management (
//                "id" bigint NOT NULL DEFAULT nextval('public.project_management_id_seq'::regclass),
//                "project_name" varchar(255) NOT NULL,
//                CONSTRAINT "pk_public_project_management" PRIMARY KEY ("id")
//            );
//            COMMENT ON TABLE public.project_management IS '项目管理表';
//            COMMENT ON COLUMN public.project_management.id IS '项目唯一标识ID';
//            COMMENT ON COLUMN public.project_management.project_name IS '项目名称';
//            """;
//
//    // 创建Schema的SQL
//    private static final String CREATE_SCHEMA_SQL = "CREATE SCHEMA IF NOT EXISTS research_%s";
//    // 检查表是否存在的SQL
//    private static final String CHECK_TABLE_EXISTS_SQL = "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = ? AND table_name = ?)";
//
//    // 创建git_commit_message表的SQL（带注释）
//    private static final String CREATE_GIT_COMMIT_MESSAGE_TABLE_SQL = """
//        CREATE TABLE IF NOT EXISTS research_%s.git_commit_message (
//            "id" bigint NOT NULL DEFAULT nextval('research_%s.smart_git_commit_message_id_seq'::regclass),
//            "create_time" timestamp,
//            "git_file_path" varchar(255),
//            "commit_message" text,
//            "commit_diff" text,
//            "role" varchar(255),
//            "member_id" bigint,
//            "project_id" bigint,
//            CONSTRAINT "pk_public_smart_git_commit_message" PRIMARY KEY ("id")
//        );
//        COMMENT ON TABLE research_%s.git_commit_message IS 'Git提交信息记录表';
//        COMMENT ON COLUMN research_%s.git_commit_message.id IS '记录唯一标识ID';
//        COMMENT ON COLUMN research_%s.git_commit_message.create_time IS '记录创建时间';
//        COMMENT ON COLUMN research_%s.git_commit_message.git_file_path IS 'Git文件路径';
//        COMMENT ON COLUMN research_%s.git_commit_message.commit_message IS '提交详细描述信息';
//        COMMENT ON COLUMN research_%s.git_commit_message.commit_diff IS 'Git代码差异描述';
//        COMMENT ON COLUMN research_%s.git_commit_message.role IS '提交者角色';
//        COMMENT ON COLUMN research_%s.git_commit_message.member_id IS '提交组织成员ID';
//        COMMENT ON COLUMN research_%s.git_commit_message.project_id IS '所属项目ID';
//    """;
//
//    // 创建team_members表的SQL（带注释）
//    private static final String CREATE_TEAM_MEMBERS_TABLE_SQL = """
//        CREATE TABLE IF NOT EXISTS research_%s.team_members (
//            "id" bigint NOT NULL DEFAULT nextval('research_%s.team_members_id_seq'::regclass),
//            "chinese_name" varchar(255) NOT NULL,
//            "email" varchar(255),
//            "position" varchar(255),
//            "responsibility" text,
//            CONSTRAINT "pk_public_team_members" PRIMARY KEY ("id")
//        );
//        COMMENT ON TABLE research_%s.team_members IS '团队成员表';
//        COMMENT ON COLUMN research_%s.team_members.id IS '组织成员ID';
//        COMMENT ON COLUMN research_%s.team_members.chinese_name IS '成员中文名';
//        COMMENT ON COLUMN research_%s.team_members.email IS '成员邮箱地址';
//        COMMENT ON COLUMN research_%s.team_members.position IS '成员岗位名称';
//        COMMENT ON COLUMN research_%s.team_members.responsibility IS '成员主要工作职责描述';
//    """;
//
//    // 创建project_management表的SQL（简化版，只包含id和name）
//    private static final String CREATE_PROJECT_MANAGEMENT_TABLE_SQL = """
//        CREATE TABLE IF NOT EXISTS research_%s.project_management (
//            "id" bigint NOT NULL DEFAULT nextval('research_%s.project_management_id_seq'::regclass),
//            "project_name" varchar(255) NOT NULL,
//            CONSTRAINT "pk_public_project_management" PRIMARY KEY ("id")
//        );
//        COMMENT ON TABLE research_%s.project_management IS '项目管理表';
//        COMMENT ON COLUMN research_%s.project_management.id IS '项目唯一标识ID';
//        COMMENT ON COLUMN research_%s.project_management.project_name IS '项目名称';
//    """;
//
//    // 创建序列的SQL
//    private static final String CREATE_SMART_GIT_COMMIT_MESSAGE_SEQ_SQL =
//            "CREATE SEQUENCE IF NOT EXISTS research_%s.smart_git_commit_message_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
//    private static final String CREATE_TEAM_MEMBERS_SEQ_SQL =
//            "CREATE SEQUENCE IF NOT EXISTS research_%s.team_members_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
//    private static final String CREATE_PROJECT_MANAGEMENT_SEQ_SQL =
//            "CREATE SEQUENCE IF NOT EXISTS research_%s.project_management_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1";
//
//    // 插入git_commit_message数据的SQL
//    private static final String INSERT_GIT_COMMIT_MESSAGE_SQL = """
//        INSERT INTO research_%s.git_commit_message
//        ("create_time", "git_file_path", "commit_message", "role", "member_id", "project_id")
//        VALUES (:createTime, :gitFilePath, :commitMessage, :role, :memberId, :projectId)
//    """;
//    // 插入team_members数据的SQL
//    private static final String INSERT_TEAM_MEMBERS_SQL = """
//        INSERT INTO research_%s.team_members
//        ("chinese_name", "email", "position", "responsibility")
//        VALUES (:chineseName, :email, :position, :responsibility)
//    """;
//    // 插入project_management数据的SQL
//    private static final String INSERT_PROJECT_MANAGEMENT_SQL = """
//        INSERT INTO research_%s.project_management
//        ("id" ,"project_name")
//        VALUES (:id , :projectName)
//    """;
//    // 批量插入git_commit_message数据的SQL
//    private static final String BATCH_INSERT_GIT_COMMIT_MESSAGE_SQL = """
//        INSERT INTO research_%s.git_commit_message
//        ("id" , "create_time", "git_file_path", "commit_message", "role", "member_id", "project_id" , "commit_diff")
//        VALUES (? , ?, ?, ?, ?, ?, ? , ?)
//    """;
//    // 批量插入team_members数据的SQL
//    private static final String BATCH_INSERT_TEAM_MEMBERS_SQL = """
//        INSERT INTO research_%s.team_members
//        ("id" , "chinese_name", "email", "position", "responsibility")
//        VALUES (? , ?, ?, ?, ?)
//    """;
//    // 批量插入project_management数据的SQL
//    private static final String BATCH_INSERT_PROJECT_MANAGEMENT_SQL = """
//        INSERT INTO research_%s.project_management
//        ("project_name")
//        VALUES (?)
//    """;
//    // 查询team_members是否存在的SQL
//    private static final String CHECK_TEAM_MEMBER_EXISTS_SQL =
//            "SELECT COUNT(*) FROM research_%s.team_members WHERE email = ?";
//    // 查询project_management是否存在的SQL
//    private static final String CHECK_PROJECT_EXISTS_SQL =
//            "SELECT COUNT(*) FROM research_%s.project_management WHERE id = ?";
//    // 更新team_members数据的SQL
//    private static final String UPDATE_TEAM_MEMBERS_SQL = """
//        UPDATE research_%s.team_members
//        SET "chinese_name" = ?, "position" = ?, "responsibility" = ?
//        WHERE email = ?
//    """;
//
//    public SchemaUtils(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//    }
//
//    /**
//     * 初始化Schema和表结构
//     * @param schemeId 租户的scheme唯一号
//     */
//    public void initSchemaAndTables(String schemeId) {
//        // 创建Schema
//        createSchemaIfNotExists(schemeId);
//        // 创建序列
//        createSequencesIfNotExists(schemeId);
//        // 创建表
//        createTablesIfNotExists(schemeId);
//    }
//
//    /**
//     * 创建Schema（如果不存在）
//     */
//    private void createSchemaIfNotExists(String schemeId) {
//        String sql = String.format(CREATE_SCHEMA_SQL, schemeId);
//        jdbcTemplate.execute(sql);
//    }
//
//    /**
//     * 创建序列（如果不存在）
//     */
//    private void createSequencesIfNotExists(String schemeId) {
//        jdbcTemplate.execute(String.format(CREATE_SMART_GIT_COMMIT_MESSAGE_SEQ_SQL, schemeId));
//        jdbcTemplate.execute(String.format(CREATE_TEAM_MEMBERS_SEQ_SQL, schemeId));
//        jdbcTemplate.execute(String.format(CREATE_PROJECT_MANAGEMENT_SEQ_SQL, schemeId));
//    }
//
//    /**
//     * 创建表（如果不存在）
//     */
//    private void createTablesIfNotExists(String schemeId) {
//        // 创建git_commit_message表
//        if (!tableExists(schemeId, "git_commit_message")) {
//            jdbcTemplate.execute(String.format(CREATE_GIT_COMMIT_MESSAGE_TABLE_SQL,
//                    schemeId, schemeId, schemeId, schemeId, schemeId, schemeId,
//                    schemeId, schemeId, schemeId, schemeId, schemeId));
//        }
//        // 创建team_members表
//        if (!tableExists(schemeId, "team_members")) {
//            jdbcTemplate.execute(String.format(CREATE_TEAM_MEMBERS_TABLE_SQL,
//                    schemeId, schemeId, schemeId, schemeId, schemeId,
//                    schemeId, schemeId, schemeId));
//        }
//        // 创建project_management表
//        if (!tableExists(schemeId, "project_management")) {
//            jdbcTemplate.execute(String.format(CREATE_PROJECT_MANAGEMENT_TABLE_SQL,
//                    schemeId,
//                    schemeId,
//                    schemeId,
//                    schemeId,
//                    schemeId));
//        }
//    }
//
//    /**
//     * 检查表是否存在
//     */
//    private boolean tableExists(String schemeId, String tableName) {
//        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
//                CHECK_TABLE_EXISTS_SQL,
//                Boolean.class,
//                "research_" + schemeId,
//                tableName
//        ));
//    }
//
//    /**
//     * 插入Git提交消息
//     * @param schemeId 租户的scheme唯一号
//     * @param message Git提交消息对象
//     * @return 插入的记录ID
//     */
//    public Long insertGitCommitMessage(String schemeId, GitCommitMessage message) {
//        String sql = String.format(INSERT_GIT_COMMIT_MESSAGE_SQL, schemeId);
//        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(message);
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[] { "id" });
//        return Objects.requireNonNull(keyHolder.getKey()).longValue();
//    }
//
//    /**
//     * 插入团队成员
//     * @param schemeId 租户的scheme唯一号
//     * @param member 团队成员对象
//     * @return 插入的记录ID
//     */
//    public Integer insertTeamMember(String schemeId, TeamMember member) {
//        String sql = String.format(INSERT_TEAM_MEMBERS_SQL, schemeId);
//        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(member);
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[] { "id" });
//        return Objects.requireNonNull(keyHolder.getKey()).intValue();
//    }
//
//    /**
//     * 插入项目
//     * @param schemeId 租户的scheme唯一号
//     * @param project 项目对象
//     * @return 插入的记录ID
//     */
//    public Integer insertProject(String schemeId, Project project) {
//        String sql = String.format(INSERT_PROJECT_MANAGEMENT_SQL, schemeId);
//        SqlParameterSource paramSource = new BeanPropertySqlParameterSource(project);
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, new String[] { "id" });
//        return Objects.requireNonNull(keyHolder.getKey()).intValue();
//    }
//
//    /**
//     * 批量插入Git提交消息
//     * @param schemeId 租户的scheme唯一号
//     * @param messages Git提交消息列表
//     * @return 插入的记录数
//     */
//    public int batchInsertGitCommitMessages(String schemeId, List<GitCommitMessage> messages) {
//        String sql = String.format(BATCH_INSERT_GIT_COMMIT_MESSAGE_SQL, schemeId);
//
//        return jdbcTemplate.batchUpdate(sql, messages, messages.size(),
//                (PreparedStatement ps, GitCommitMessage message) -> {
//                    ps.setLong(1, message.getId());
//                    ps.setTimestamp(2, message.getCreateTime() != null ?  new java.sql.Timestamp(message.getCreateTime().getTime()) : null);
//                    ps.setString(3, message.getGitFilePath());
//                    ps.setString(4, message.getCommitMessage());
//                    ps.setString(5, message.getRole());
//                    ps.setLong(6, message.getMemberId());
//                    ps.setLong(7, message.getProjectId());
//                    ps.setString(8, message.getCommitDiff());
//                }
//        ).length;
//    }
//
//    /**
//     * 批量插入团队成员
//     * @param schemeId 租户的scheme唯一号
//     * @param members 团队成员列表
//     * @return 插入的记录数
//     */
//    public int batchInsertTeamMembers(String schemeId, List<TeamMember> members) {
//        String sql = String.format(BATCH_INSERT_TEAM_MEMBERS_SQL, schemeId);
//
//        return jdbcTemplate.batchUpdate(sql, members, members.size(),
//                (PreparedStatement ps, TeamMember member) -> {
//                    ps.setLong(1, member.getId());
//                    ps.setString(2, member.getChineseName());
//                    ps.setString(3, member.getEmail());
//                    ps.setString(4, member.getPosition());
//                    ps.setString(5, member.getResponsibility());
//                }
//        ).length;
//    }
//
//    /**
//     * 批量插入项目
//     * @param schemeId 租户的scheme唯一号
//     * @param projects 项目列表
//     * @return 插入的记录数
//     */
//    public int batchInsertProjects(String schemeId, List<Project> projects) {
//        String sql = String.format(BATCH_INSERT_PROJECT_MANAGEMENT_SQL, schemeId);
//
//        return jdbcTemplate.batchUpdate(sql, projects, projects.size(),
//                (PreparedStatement ps, Project project) -> {
//                    ps.setString(1, project.getProjectName());
//                }
//        ).length;
//    }
//
//    /**
//     * 更新团队成员，如果不存在则插入
//     * @param schemeId 租户的scheme唯一号
//     * @param member 团队成员对象
//     */
//    public void upsertTeamMember(String schemeId, TeamMember member) {
//        if (teamMemberExists(schemeId, member.getEmail())) {
//            updateTeamMember(schemeId, member);
//        } else {
//            insertTeamMember(schemeId, member);
//        }
//    }
//
//    /**
//     * 检查团队成员是否存在
//     */
//    public boolean teamMemberExists(String schemeId, String email) {
//        String sql = String.format(CHECK_TEAM_MEMBER_EXISTS_SQL, schemeId);
//        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
//        return count != null && count > 0;
//    }
//
//    /**
//     * 检查项目是否存在
//     */
//    public boolean projectExists(String schemeId, Long projectId) {
//        String sql = String.format(CHECK_PROJECT_EXISTS_SQL, schemeId);
//        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, projectId);
//        return count != null && count > 0;
//    }
//
//    /**
//     * 更新团队成员
//     */
//    private void updateTeamMember(String schemeId, TeamMember member) {
//        String sql = String.format(UPDATE_TEAM_MEMBERS_SQL, schemeId);
//        jdbcTemplate.update(sql,
//                member.getChineseName(),
//                member.getPosition(),
//                member.getResponsibility(),
//                member.getEmail()
//        );
//    }
//
//    /**
//     * Git提交消息实体类
//     */
//    @Data
//    public static class GitCommitMessage {
//        private Long id;
//        private java.util.Date createTime;
//        private String gitFilePath;
//        private String commitMessage;
//        private String role;
//        private Long memberId;
//        private Long projectId;
//        private String commitDiff ;
//    }
//
//    /**
//     * 团队成员实体类
//     */
//    @Data
//    public static class TeamMember {
//        private Long id;
//        private String chineseName;
//        private String email;
//        private String position;
//        private String responsibility;
//    }
//
//    /**
//     * 项目实体类
//     */
//    @Data
//    public static class Project {
//        private Long id;
//        private String projectName;
//    }
//}
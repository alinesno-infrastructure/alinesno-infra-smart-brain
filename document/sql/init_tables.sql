-- 启用pgvector扩展（如果尚未启用）
-- 该扩展提供向量存储和相似度计算功能
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建向量文档存储主表
-- 用于存储文档内容及其1024维度的向量嵌入
CREATE TABLE IF NOT EXISTS alinesno_search_vector_document (
    -- 自增主键ID
    id BIGSERIAL PRIMARY KEY,
    
    -- 数据集ID，支持多租户场景
    dataset_id BIGSERIAL NOT NULL,
    
    -- 索引/集合名称，用于文档分组
    index_name VARCHAR(255) NOT NULL,
    
    -- 文档元数据字段
    document_title TEXT,               -- 文档标题
    document_desc TEXT,               -- 文档描述
    document_content TEXT,            -- 文档完整内容
    
    -- 向量相关字段
    document_embedding VECTOR(1024),  -- 1024维度的文档嵌入向量（BGE模型输出）
    token_size INT,                   -- 文档token数量
    doc_chunk TEXT,                   -- 文档分块内容（如使用分块策略）
    score REAL,                       -- 相关性评分
    
    -- 来源信息字段
    page INTEGER,                     -- 原始页码（如PDF文档）
    source_file VARCHAR(255),         -- 来源文件名
    source_url TEXT,                  -- 来源URL链接
    source_type VARCHAR(50),          -- 来源类型（如pdf/html/news等）
    
    -- 作者信息
    author VARCHAR(255),              -- 文档作者
    
    -- 系统记录字段
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP  -- 创建时间
);

-- 创建IVFFlat向量索引（用于加速余弦相似度搜索）
-- lists参数指定聚类中心数量，影响查询速度和精度
CREATE INDEX ON alinesno_search_vector_document USING ivfflat (document_embedding vector_cosine_ops) WITH (lists = 100);

-- 创建全文检索GIN索引（英文语系）
-- 联合索引文档标题和内容，支持关键词搜索
CREATE INDEX idx_fts_alinesno_search_vector_document ON alinesno_search_vector_document USING gin(to_tsvector('english', document_title || ' ' || document_content));
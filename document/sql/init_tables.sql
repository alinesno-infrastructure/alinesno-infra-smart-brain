/******************************************/
/*   DatabaseName = public   */
/*   TableName = alinesno_search_vector_document   */
/******************************************/
CREATE TABLE "public"."alinesno_search_vector_document"
(
 "id" bigint NOT NULL DEFAULT nextval('alinesno_search_vector_document_id_seq'::regclass) ,
 "dataset_id" bigint NOT NULL ,
 "index_name" varchar(255) NOT NULL ,
 "document_title" text ,
 "document_desc" text ,
 "document_content" text ,
 "document_embedding" vector ,
 "token_size" integer ,
 "doc_chunk" text ,
 "score" real ,
 "page" integer ,
 "source_file" varchar(255) ,
 "source_url" text ,
 "source_type" varchar(50) ,
 "author" varchar(255) ,
 "created_at" timestamp without time zone DEFAULT CURRENT_TIMESTAMP ,
CONSTRAINT "pk_public_alinesno_search_vector_document" PRIMARY KEY ("id")
)
WITH (
    FILLFACTOR = 100,
    OIDS = FALSE
)
;

CREATE INDEX "alinesno_search_vector_document_document_embedding_idx"
ON "public"."alinesno_search_vector_document" USING ivfflat ( "document_embedding" )
;

CREATE INDEX "idx_fts_alinesno_search_vector_document"
ON "public"."alinesno_search_vector_document" USING gin ( to_tsvector('english'::regconfig, (document_title || ' '::text) || document_content) )
;

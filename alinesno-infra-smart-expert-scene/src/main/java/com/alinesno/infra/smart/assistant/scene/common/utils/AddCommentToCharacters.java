package com.alinesno.infra.smart.assistant.scene.common.utils;

import com.alinesno.infra.common.core.utils.StringUtils;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.CommentMark;
import com.spire.doc.documents.CommentMarkType;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.TextSelection;
import com.spire.doc.fields.Comment;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

// 定义 Revision 类
@Slf4j
public class AddCommentToCharacters {
    /**
     * 添加批注信息
     *
     * @param filePath     文档文件路径
     * @param combinedJson 包含修订信息的数组
     * @return 临时文件路径
     */
    public static String addCommentToCharacters(String filePath, List<Revision> combinedJson) {
        Document doc = new Document();
        doc.loadFromFile(filePath);

        int count = 1;

        for (Revision revision : combinedJson) {
            System.out.println("revision.originalText:" + revision.getOriginalText());

            if(!StringUtils.isEmpty(revision.getOriginalText())){
                TextSelection[] selections = doc.findAllString(revision.getOriginalText(), true, false);

                if(selections == null){
                    continue;
                }

                for (TextSelection selection : selections) {
                    System.out.println(selection.getSelectedText());

                    Paragraph para = selection.getAsOneRange().getOwnerParagraph();
                    int index = para.getChildObjects().indexOf(selection.getAsOneRange());

                    CommentMark start = new CommentMark(doc);
                    start.setCommentId(count);
                    start.setType(CommentMarkType.Comment_Start);

                    CommentMark end = new CommentMark(doc);
                    end.setType(CommentMarkType.Comment_End);
                    end.setCommentId(count);

                    String str =  "批注原因:" + revision.getReason() +
                            "\r\n" +
                            "修改建议：" + revision.getSuggestedTexts();
                    Comment comment = new Comment(doc);
                    comment.getFormat().setCommentId(1);
                    comment.getBody().addParagraph().appendText(str);
                    comment.getFormat().setInitial("CM");

                    para.getChildObjects().insert(index, start);
                    para.getChildObjects().insert(index + 1, selection.getAsOneRange());
                    para.getChildObjects().insert(index + 2, end);
                    para.getChildObjects().insert(index + 3, comment);

                    count++;
                }
            }

        }

        try {
            File tempFile = File.createTempFile("comments_", ".docx");
            String tempFilePath = tempFile.getAbsolutePath();

            doc.saveToFile(tempFilePath, FileFormat.Docx_2013);
            doc.dispose();

            return tempFilePath;
        } catch (IOException e) {
            log.error("添加批注信息失败:", e);
            return null;
        }
    }
}    
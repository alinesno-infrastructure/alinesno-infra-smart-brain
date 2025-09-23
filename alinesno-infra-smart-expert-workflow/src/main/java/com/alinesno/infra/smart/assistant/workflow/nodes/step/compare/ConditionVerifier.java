package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

/**
 * 条件校验器接口
 * <p>
 * 定义了条件校验的基本操作，用于判断两个值是否满足特定的校验条件。
 * 实现类需要提供具体的支持条件类型和校验逻辑。
 * </p>
 */
public interface ConditionVerifier {

    /**
     * 判断当前校验器是否支持指定的校验条件类型
     *
     * @param condition 校验条件类型标识符
     * @return 如果支持该校验条件类型返回true，否则返回false
     */
    boolean isSupported(String condition);

    /**
     * 执行具体的校验操作
     *
     * @param sourceData 源数据，参与校验的第一个值
     * @param targetData 目标数据，参与校验的第二个值
     * @return 校验结果，满足校验条件返回true，否则返回false
     */
    boolean verify(Object sourceData, String targetData);

}
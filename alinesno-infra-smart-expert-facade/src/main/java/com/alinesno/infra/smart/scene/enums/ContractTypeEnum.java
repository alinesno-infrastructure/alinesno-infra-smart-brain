package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同类型枚举类
 */
@Getter
public enum ContractTypeEnum {
    SALES_CONTRACT("37", "买卖合同"),
    SUPPLY_CONTRACT("40", "供用电、水、气、热力合同"),
    GIFT_CONTRACT("1", "赠与合同"),
    LOAN_CONTRACT("2", "借款合同"),
    LEASE_CONTRACT("4", "租赁合同"),
    FINANCING_LEASE_CONTRACT("7", "融资租赁合同"),
    WORK_CONTRACT("9", "承揽合同"),
    CONSTRUCTION_PROJECT_CONTRACT("13", "建设工程合同"),
    TRANSPORT_CONTRACT("17", "运输合同"),
    TECHNOLOGY_CONTRACT("18", "技术合同"),
    CUSTODY_CONTRACT("30", "保管合同"),
    WAREHOUSING_CONTRACT("31", "仓储合同"),
    AGENCY_CONTRACT("32", "委托合同"),
    COMMISSION_AGENCY_CONTRACT("34", "行纪合同"),
    INTERMEDIARY_CONTRACT("35", "中介合同"),
    PROPERTY_SERVICE_CONTRACT("33", "物业服务合同"),
    LABOR_SERVICE_CONTRACT("41", "劳务合同"),
    FACTORING_CONTRACT("8", "保理合同"),
    PARTNERSHIP_CONTRACT("36", "合伙合同"),
    PRIVATE_LOAN_CONTRACT("3", "民间借贷合同"),
    GENERAL_HOUSING_LEASE_CONTRACT("5", "一般房屋租赁合同"),
    BUILDING_MATERIAL_LEASE_CONTRACT("6", "建材租赁合同"),
    PERSONAL_LABOR_WORK_AGREEMENT("10", "个人劳务承揽(兼职、顾问)协议"),
    TRAINING_SERVICE_PERIOD_AGREEMENT("11", "培训服务期协议"),
    LABOR_OUTSOURCING_CONTRACT("12", "劳务外包合同"),
    CONSULTING_SERVICE_CONTRACT("25", "咨询服务合同"),
    CONSTRUCTION_DESIGN_CONTRACT("14", "建设工程设计合同"),
    CONSTRUCTION_CONTRACT("15", "建设工程施工合同"),
    CONSTRUCTION_SUPERVISION_CONTRACT("16", "建设工程监理合同"),
    TECHNOLOGY_DEVELOPMENT_CONTRACT("19", "技术开发合同"),
    SOFTWARE_DEVELOPMENT_SERVICE_AGREEMENT("20", "软件开发服务协议"),
    SOFTWARE_TECHNOLOGY_DEVELOPMENT_CONTRACT("21", "软件技术开发合同"),
    TECHNOLOGY_TRANSFER_CONTRACT("22", "技术转让合同"),
    TRADEMARK_TRANSFER_CONTRACT("23", "注册商标转让合同"),
    COPYRIGHT_TRANSFER_CONTRACT("24", "著作权转让合同"),
    TECHNOLOGY_SERVICE_CONTRACT("26", "技术服务合同"),
    TECHNOLOGY_LICENSE_CONTRACT("27", "技术许可合同"),
    TRADEMARK_LICENSE_CONTRACT("28", "注册商标许可使用合同"),
    COPYRIGHT_LICENSE_CONTRACT("29", "著作权许可使用合同"),
    BUILDING_MATERIAL_SALES_CONTRACT("38", "建材买卖合同"),
    IT_SOFTWARE_SALES_CONTRACT("39", "IT软件买卖合同"),
    COMPANY_EQUITY_FINANCE_TYPE("42", "公司股权金融类型"),
    EQUITY_HOLDING_AGREEMENT("43", "股权代持合同"),
    INVESTMENT_ATTRACTION_CONTRACT("44", "招商引资合同"),
    PRIVATE_EQUITY_INVESTMENT_CAPITAL_INCREASE_AGREEMENT("45", "私募股权投资增资协议"),
    PRIVATE_EQUITY_INVESTMENT_SHAREHOLDER_AGREEMENT("46", "私募股权投资股东协议"),
    FRANCHISE_CONTRACT("47", "特许经营(许可加盟)合同"),
    SHAREHOLDER_CONTRIBUTION_AGREEMENT("48", "股东出资协议"),
    EQUITY_TRANSFER_CONTRACT("49", "股权转让合同"),
    LIMITED_PARTNERSHIP_AGREEMENT("50", "有限合伙企业合伙协议"),
    GENERAL_PARTNERSHIP_AGREEMENT("51", "普通合伙企业合伙协议"),
    IP_PRODUCTION_CONTRACT("72", "IP委托制作(视频、音频)类合同"),
    OTHER("73", "其他");

    private final String scenarioId;
    private final String title;

    ContractTypeEnum(String scenarioId, String title) {
        this.scenarioId = scenarioId;
        this.title = title;
    }

    // 返回List<Map<String , String>>列表
    public static List<Map<String, String>> getList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (ContractTypeEnum type : values()) {
            Map<String, String> map = new HashMap<>();
            map.put("scenarioId", type.scenarioId);
            map.put("title", type.title);
            list.add(map);
        }
        return list;
    }

    public static ContractTypeEnum getByScenarioId(String scenarioId) {
        for (ContractTypeEnum type : values()) {
            if (type.scenarioId.equals(scenarioId)) {
                return type;
            }
        }
        return null;
    }
}    
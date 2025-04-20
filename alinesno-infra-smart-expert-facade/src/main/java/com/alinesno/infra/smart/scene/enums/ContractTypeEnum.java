package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum ContractTypeEnum {

    CUSTOM_SCENARIO("-1", "自定义场景", "该场景为用户自行创建的自定义场景，用于满足特定的个性化需求。"),
    SALE_CONTRACT("37", "买卖合同", "用于规范商品或服务的买卖双方权利和义务的合同场景。"),
    SUPPLY_CONTRACT("40", "供用电、水、气、热力合同", "规定了供应方和使用方在电、水、气、热力供应过程中的权利和义务的合同场景。"),
    GIFT_CONTRACT("1", "赠与合同", "明确赠与人和受赠人之间赠与行为的权利义务关系的合同场景。"),
    LOAN_CONTRACT("2", "借款合同", "用于约定借款人和贷款人之间借款相关事宜的合同场景。"),
    LEASE_CONTRACT("4", "租赁合同", "规范出租人和承租人之间租赁关系的合同场景。"),
    FINANCING_LEASE_CONTRACT("7", "融资租赁合同", "涉及三方主体，规范融资租赁业务中各方权利义务的合同场景。"),
    CONTRACTING_CONTRACT("9", "承揽合同", "规定定作人和承揽人之间承揽工作相关权利义务的合同场景。"),
    CONSTRUCTION_CONTRACT("13", "建设工程合同", "用于建设工程相关项目中，明确发包方和承包方权利义务的合同场景。"),
    TRANSPORT_CONTRACT("17", "运输合同", "规范承运人和托运人之间运输服务权利义务的合同场景。"),
    TECHNOLOGY_CONTRACT("18", "技术合同", "涉及技术开发、转让、咨询或服务等技术相关活动的合同场景。"),
    CUSTODY_CONTRACT("30", "保管合同", "约定保管人和寄存人之间保管物品权利义务的合同场景。"),
    WAREHOUSING_CONTRACT("31", "仓储合同", "用于规范仓储保管人和存货人之间仓储业务权利义务的合同场景。"),
    ENTRUSTMENT_CONTRACT("32", "委托合同", "明确委托人和受托人之间委托事务处理权利义务的合同场景。"),
    FACTORING_CONTRACT("8", "保理合同", "涉及保理业务中，规范保理商和供应商等主体权利义务的合同场景。"),
    BROKERAGE_CONTRACT("34", "行纪合同", "规定行纪人和委托人之间行纪业务权利义务的合同场景。"),
    INTERMEDIARY_CONTRACT("35", "中介合同", "用于规范中介人和委托人之间中介服务权利义务的合同场景。"),
    PROPERTY_SERVICE_CONTRACT("33", "物业服务合同", "明确物业服务企业和业主之间物业服务权利义务的合同场景。"),
    LABOR_CONTRACT("41", "劳务合同", "规范劳务提供方和劳务需求方之间劳务关系的合同场景。"),
    PARTNERSHIP_CONTRACT("36", "合伙合同", "用于约定合伙人之间合伙事务权利义务的合同场景。"),
    OTHER("73", "其他", "涵盖其他未明确分类的合同场景。"),
    PRIVATE_LOAN_CONTRACT("3", "民间借贷合同", "规范自然人、法人、其他组织之间借贷行为的合同场景。"),
    GENERAL_HOUSE_LEASE_CONTRACT("5", "一般房屋租赁合同", "用于规范房屋出租人和承租人之间租赁关系的合同场景。"),
    BUILDING_MATERIALS_LEASE_CONTRACT("6", "建材租赁合同", "规定建材出租人和承租人之间租赁建材权利义务的合同场景。"),
    INDIVIDUAL_LABOR_CONTRACTING_AGREEMENT("10", "个人劳务承揽(兼职、顾问)协议", "用于个人提供劳务承揽服务，明确双方权利义务的合同场景。"),
    TRAINING_SERVICE_PERIOD_AGREEMENT("11", "培训服务期协议", "约定培训提供方和接受方在培训服务期间权利义务的合同场景。"),
    LABOR_OUTSOURCING_CONTRACT("12", "劳务外包合同", "规范劳务外包方和承接方之间劳务外包业务权利义务的合同场景。"),
    CONSULTING_SERVICE_CONTRACT("25", "咨询服务合同", "用于约定咨询服务提供方和需求方之间权利义务的合同场景。"),
    CONSTRUCTION_DESIGN_CONTRACT("14", "建设工程设计合同", "明确建设工程设计方和委托方之间设计业务权利义务的合同场景。"),
    CONSTRUCTION_CONSTRUCTION_CONTRACT("15", "建设工程施工合同", "规范建设工程施工过程中发包方和承包方权利义务的合同场景。"),
    CONSTRUCTION_SUPERVISION_CONTRACT("16", "建设工程监理合同", "用于约定建设工程监理方和委托方之间监理业务权利义务的合同场景。"),
    TECHNOLOGY_DEVELOPMENT_CONTRACT("19", "技术开发合同", "涉及技术开发项目，明确开发方和委托方权利义务的合同场景。"),
    SOFTWARE_DEVELOPMENT_SERVICE_AGREEMENT("20", "软件开发服务协议", "用于规范软件开发服务提供方和需求方之间权利义务的合同场景。"),
    SOFTWARE_TECHNOLOGY_DEVELOPMENT_CONTRACT("21", "软件技术开发合同", "专注于软件技术开发，明确双方权利义务的合同场景。"),
    TECHNOLOGY_TRANSFER_CONTRACT("22", "技术转让合同", "规定技术转让方和受让方之间技术转让权利义务的合同场景。"),
    TRADEMARK_TRANSFER_CONTRACT("23", "注册商标转让合同", "用于规范注册商标转让过程中转让方和受让方权利义务的合同场景。"),
    COPYRIGHT_TRANSFER_CONTRACT("24", "著作权转让合同", "明确著作权转让方和受让方之间权利义务的合同场景。"),
    TECHNOLOGY_SERVICE_CONTRACT("26", "技术服务合同", "约定技术服务提供方和需求方之间技术服务权利义务的合同场景。"),
    TECHNOLOGY_LICENSE_CONTRACT("27", "技术许可合同", "用于规范技术许可方和被许可方之间技术许可权利义务的合同场景。"),
    TRADEMARK_LICENSE_CONTRACT("28", "注册商标许可使用合同", "明确注册商标许可使用过程中许可方和被许可方权利义务的合同场景。"),
    COPYRIGHT_LICENSE_CONTRACT("29", "著作权许可使用合同", "规定著作权许可使用过程中许可方和被许可方权利义务的合同场景。"),
    BUILDING_MATERIALS_SALE_CONTRACT("38", "建材买卖合同", "用于规范建材买卖双方权利义务的合同场景。"),
    IT_SOFTWARE_SALE_CONTRACT("39", "IT软件买卖合同", "规定IT软件买卖双方权利义务的合同场景。"),
    COMPANY_EQUITY_FINANCE_TYPE("42", "公司股权金融类型", "涉及公司股权相关金融业务，规范各方权利义务的合同场景。"),
    EQUITY_TRUST_CONTRACT("43", "股权代持合同", "用于约定股权代持过程中委托方和代持方权利义务的合同场景。"),
    INVESTMENT_CONTRACT("44", "招商引资合同", "规范招商引资过程中投资方和引资方权利义务的合同场景。"),
    PRIVATE_EQUITY_INVESTMENT_CAPITAL_INCREASE_AGREEMENT("45", "私募股权投资增资协议", "用于私募股权投资增资业务，明确各方权利义务的合同场景。"),
    PRIVATE_EQUITY_INVESTMENT_SHAREHOLDER_AGREEMENT("46", "私募股权投资股东协议", "规范私募股权投资中股东之间权利义务的合同场景。"),
    FRANCHISE_CONTRACT("47", "特许经营(许可加盟)合同", "用于规范特许经营过程中特许方和加盟方权利义务的合同场景。"),
    SHAREHOLDER_CONTRIBUTION_AGREEMENT("48", "股东出资协议", "明确股东在出资过程中权利义务的合同场景。"),
    EQUITY_TRANSFER_CONTRACT("49", "股权转让合同", "规定股权转让过程中转让方和受让方权利义务的合同场景。"),
    LIMITED_PARTNERSHIP_AGREEMENT("50", "有限合伙企业合伙协议", "用于规范有限合伙企业中合伙人之间权利义务的合同场景。"),
    GENERAL_PARTNERSHIP_AGREEMENT("51", "普通合伙企业合伙协议", "明确普通合伙企业中合伙人之间权利义务的合同场景。"),
    GENERAL_CONTRIBUTION_AGREEMENT("52", "一般出资协议", "用于约定一般出资行为中各方权利义务的合同场景。"),
    EQUITY_TRUST_DELEGATED_HOLDING_AGREEMENT("53", "股权代持/委托持股协议", "规范股权代持或委托持股过程中相关方权利义务的合同场景。"),
    ADVERTISING_CONTRACT("54", "广告合同", "用于规范广告业务中广告主和广告服务提供方之间权利义务的合同场景。"),
    ADVERTISING_ENDORSEMENT_CONTRACT("55", "广告代言合同", "明确广告代言过程中广告主、代言人和相关方权利义务的合同场景。"),
    ADVERTISING_PUBLISHING_CONTRACT("56", "广告发布合同", "规定广告发布过程中广告主和发布方之间权利义务的合同场景。"),
    EMPLOYMENT_CONTRACT("57", "劳动合同", "用于规范用人单位和劳动者之间劳动关系的合同场景。"),
    SETTLEMENT_AGREEMENT("58", "协商解除劳动关系协议", "用于约定用人单位和劳动者协商解除劳动关系过程中权利义务的合同场景。"),
    NON_COMPETITION_AGREEMENT("59", "竞业限制协议", "规定劳动者在离职后一定期限内不得从事特定竞争业务的合同场景。"),
    LABOR_DISPATCH_CONTRACT("60", "劳动派遣合同", "用于规范劳动派遣单位、用工单位和劳动者之间权利义务的合同场景。"),
    CONFIDENTIALITY_AGREEMENT("61", "保密协议", "明确保密义务人和权利人之间保密权利义务的合同场景。"),
    DIVORCE_AGREEMENT("62", "离婚协议", "用于约定夫妻双方离婚过程中财产分割、子女抚养等权利义务的合同场景。"),
    GUARANTEE_CONTRACT("63", "担保合同", "涉及担保业务，规范担保人和债权人之间权利义务的合同场景。"),
    SURETYSHIP_CONTRACT("64", "保证合同", "用于约定保证人和债权人之间保证权利义务的合同场景。"),
    PLEDGE_CONTRACT("65", "质押合同", "规定质押人和质权人之间质押权利义务的合同场景。"),
    MORTGAGE_CONTRACT("66", "抵押合同", "用于规范抵押人和抵押权人之间抵押权利义务的合同场景。"),
    LIEN_CONTRACT("67", "留置合同", "明确留置权人和债务人之间留置权利义务的合同场景。"),
    EARNEST_MONEY_CONTRACT("68", "定金合同", "用于约定定金支付方和收受方之间定金权利义务的合同场景。"),
    CONTRACT_TERMINATION("69", "解除合同", "规定合同解除过程中双方权利义务的合同场景。"),
    SUPPLEMENTARY_CONTRACT("70", "补充合同", "用于对原合同进行补充约定，明确双方权利义务的合同场景。"),
    INTELLECTUAL_PROPERTY_PLEDGE_CONTRACT("71", "知识产权(专利权、商标权、著作权)质押合同", "规范知识产权质押过程中质押方和质权方权利义务的合同场景。"),
    IP_COMMISSION_CONTRACT("72", "IP委托制作(视频、音频)类合同", "用于约定IP委托制作过程中委托方和制作方权利义务的合同场景。");

    private final String scenarioId;
    private final String title;
    private final String desc;

    ContractTypeEnum(String scenarioId, String title, String desc) {
        this.scenarioId = scenarioId;
        this.title = title;
        this.desc = desc;
    }

    /**
     * 通过scenarioId获取到ContractTypeEnum
     */
    public static ContractTypeEnum getByScenarioId(String scenarioId) {
        for (ContractTypeEnum scenario : ContractTypeEnum.values()) {
            if (scenario.scenarioId.equals(scenarioId)) {
                return scenario;
            }
        }
        return null;
    }

    /**
     * 获取到List<Map<String , String>>
     */
    public static List<Map<String, String>> getContractScenarioList() {

        List<Map<String, String>> list = new ArrayList<>();
        for (ContractTypeEnum scenario : ContractTypeEnum.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("scenarioId", scenario.scenarioId);
            map.put("title", scenario.title);
            map.put("desc", scenario.desc);
            list.add(map);
        }
        return list;
    }

}
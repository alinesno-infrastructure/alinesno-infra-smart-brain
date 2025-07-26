<template>
    <div class="ai-audit-container">
        <!-- Step indicator -->
        <el-steps class="audit-step-container" :active="activeStep" finish-status="success" simple>
            <el-step title="选择审核规则" />
            <el-step title="审核结果" />
        </el-steps>

        <!-- Step 1: Rule Selection -->
        <div v-if="activeStep === 1" class="rule-selection">
            <div class="selection-header">
                <p>请选择要应用的审核规则分类和具体规则</p>
            </div>

            <el-scrollbar style="height: calc(100vh - 370px)">
                <el-collapse v-model="activeCategories" accordion style="border:0px;">
                    <el-collapse-item 
                        v-for="category in ruleCategories" 
                        :key="category.id" 
                        :name="category.id"
                        class="category-item"
                    >
                        <template #title>
                            <div class="category-header">
                                <el-icon v-if="category.icon" :size="20">
                                    <component :is="category.icon" />
                                </el-icon>
                                <span>{{ category.name }}</span>
                                <el-tag size="large">{{ category.ruleCount }}条规则</el-tag>
                                <el-checkbox 
                                    v-model="selectedAllInCategory[category.id]" 
                                    @click.stop
                                    @change="(val) => toggleSelectAllCategory(category.id, val)"
                                >
                                    全选
                                </el-checkbox>
                            </div>
                        </template>

                        <el-checkbox-group v-model="selectedRules" class="rule-list">
                            <el-checkbox 
                                v-for="rule in getRulesByCategory(category.id)" 
                                :key="rule.id" 
                                :label="rule.id"
                                class="rule-item"
                            >
                                <div class="rule-content">
                                    <div class="rule-header">
                                        <span class="rule-name">{{ rule.ruleName }}</span>
                                        <el-tag :type="getRiskTagType(rule.riskLevel)" size="small">
                                            {{ formatRiskLevel(rule.riskLevel) }}
                                        </el-tag>
                                    </div>
                                    <p class="rule-desc">{{ rule.ruleDescription }}</p>
                                </div>
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-collapse-item>
                </el-collapse>
            </el-scrollbar>
            <div class="action-buttons">
                <el-button type="primary" size="large" @click="startAudit" :disabled="!selectedRules.length">
                    开始审核 ({{ selectedRules.length }}条规则已选)
                </el-button>
            </div>

        </div>

        <!-- Step 2: Audit Results -->
        <div v-if="activeStep === 2" class="audit-results-view">
            <div class="header">
                <h1>AI文档审核结果</h1>
                <div class="actions">
                    <el-button type="primary" text bg icon="Download" @click="exportResults">导出审核结果</el-button>
                    <el-button type="info" text bg icon="Refresh" @click="backToSelection">重新选择规则</el-button>
                </div>
            </div>

            <div class="filter-section">
                <div class="filter-group">
                    <span class="filter-label">风险等级：</span>
                    <el-checkbox-group v-model="filterRiskLevels">
                        <el-checkbox label="high">高风险</el-checkbox>
                        <el-checkbox label="medium">中风险</el-checkbox>
                        <el-checkbox label="low">低风险</el-checkbox>
                    </el-checkbox-group>
                </div>
            </div>

            <el-scrollbar style="height:calc(100vh - 380px)">
                <div class="audit-results">
                    <el-collapse v-model="activeNames" accordion>
                        <template v-for="(results, type) in filteredResults" :key="type">
                            <el-collapse-item :name="type">
                                <template #title>
                                    <div class="collapse-title">
                                        <span>{{ type }}</span>
                                        <el-tag :type="getRiskTagType(getHighestRiskLevel(results))">
                                            {{ results.length }}个问题
                                        </el-tag>
                                        <div class="risk-tags">
                                            <el-tag v-if="hasRiskLevel(results, 'high')" type="danger">
                                                高风险: {{ countRiskLevel(results, 'high') }}
                                            </el-tag>
                                            <el-tag v-if="hasRiskLevel(results, 'medium')" type="warning">
                                                中风险: {{ countRiskLevel(results, 'medium') }}
                                            </el-tag>
                                            <el-tag v-if="hasRiskLevel(results, 'low')" type="success">
                                                低风险: {{ countRiskLevel(results, 'low') }}
                                            </el-tag>
                                        </div>
                                    </div>
                                </template>

                                <div v-for="(item, index) in results" 
                                    :key="index" 
                                    class="audit-item"
                                    :data-risk="item.riskLevel">
                                    <div class="item-header">
                                        <el-tag :type="getRiskTagType(item.riskLevel)">
                                            {{ formatRiskLevel(item.riskLevel) }}
                                        </el-tag>
                                        <span class="item-title">{{ item.checkItem }}</span>
                                        <el-button v-if="item.position" type="text"
                                            @click="navigateToPosition(item.position)">
                                            <el-icon>
                                                <Position />
                                            </el-icon> 定位
                                        </el-button>
                                    </div>

                                    <div class="item-content">
                                        <div class="problem-section">
                                            <h4>检查详情：</h4>
                                            <p>{{ item.checkDetails }}</p>
                                            <div v-if="item.context" class="problem-context">
                                                <el-tag type="info">上下文</el-tag>
                                                <pre>{{ item.context }}</pre>
                                            </div>
                                        </div>

                                        <div class="solution-section">
                                            <h4>修改建议：</h4>
                                            <p>{{ item.suggestion || '暂无具体建议' }}</p>
                                        </div>
                                    </div>
                                </div>
                            </el-collapse-item>
                        </template>
                    </el-collapse>
                </div>
            </el-scrollbar>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Position, Document, Connection, Grid, School, Checked } from '@element-plus/icons-vue'

// Step management
const activeStep = ref(1)

// Rule selection data
const ruleCategories = ref([
    { id: 1, name: '语言规范', icon: Document, ruleCount: 8, groupType: 'audit' },
    { id: 2, name: '逻辑结构', icon: Connection, ruleCount: 3, groupType: 'audit' },
    { id: 3, name: '格式规范', icon: Grid, ruleCount: 5, groupType: 'audit' },
    { id: 4, name: '学术规范', icon: School, ruleCount: 4, groupType: 'audit' },
    { id: 5, name: '内容真实', icon: Checked, ruleCount: 2, groupType: 'audit' }
])

const allRules = ref([
    // Language rules
    { id: 101, groupId: 1, ruleName: '错别字检查', riskLevel: 'high', ruleDescription: '检查文档中的错别字和形似字错误' },
    { id: 102, groupId: 1, ruleName: '标点符号', riskLevel: 'medium', ruleDescription: '检查标点符号使用是否正确' },
    { id: 103, groupId: 1, ruleName: '语法规范', riskLevel: 'high', ruleDescription: '检查语法是否正确' },
    { id: 104, groupId: 1, ruleName: '用词恰当', riskLevel: 'medium', ruleDescription: '检查用词是否恰当' },
    { id: 105, groupId: 1, ruleName: '语句通顺', riskLevel: 'high', ruleDescription: '检查语句是否通顺' },
    { id: 106, groupId: 1, ruleName: '成语检查', riskLevel: 'medium', ruleDescription: '检查成语使用是否正确' },
    { id: 107, groupId: 1, ruleName: '风格统一', riskLevel: 'medium', ruleDescription: '检查语言风格是否统一' },
    { id: 108, groupId: 1, ruleName: '场景风格', riskLevel: 'medium', ruleDescription: '检查风格是否符合场景' },
    
    // Logic rules
    { id: 201, groupId: 2, ruleName: '表达严谨', riskLevel: 'high', ruleDescription: '检查表达是否严谨' },
    { id: 202, groupId: 2, ruleName: '内容完整', riskLevel: 'high', ruleDescription: '检查内容是否完整' },
    { id: 203, groupId: 2, ruleName: '逻辑连贯', riskLevel: 'medium', ruleDescription: '检查逻辑是否连贯' },
    
    // Format rules
    { id: 301, groupId: 3, ruleName: '格式规范', riskLevel: 'low', ruleDescription: '检查文档格式是否符合要求' },
    { id: 302, groupId: 3, ruleName: '排版规范', riskLevel: 'low', ruleDescription: '检查排版是否符合规范' },
    { id: 303, groupId: 3, ruleName: '字体统一', riskLevel: 'low', ruleDescription: '检查字体是否统一' },
    { id: 304, groupId: 3, ruleName: '间距规范', riskLevel: 'low', ruleDescription: '检查行距段距是否符合要求' },
    { id: 305, groupId: 3, ruleName: '页眉页脚', riskLevel: 'low', ruleDescription: '检查页眉页脚设置' },
    
    // Academic rules
    { id: 401, groupId: 4, ruleName: '引用规范', riskLevel: 'high', ruleDescription: '检查引用是否规范' },
    { id: 402, groupId: 4, ruleName: '参考文献', riskLevel: 'high', ruleDescription: '检查参考文献格式' },
    { id: 403, groupId: 4, ruleName: '学术诚信', riskLevel: 'high', ruleDescription: '检查学术诚信问题' },
    { id: 404, groupId: 4, ruleName: '数据引用', riskLevel: 'medium', ruleDescription: '检查数据引用规范' },
    
    // Content rules
    { id: 501, groupId: 5, ruleName: '数据准确', riskLevel: 'high', ruleDescription: '检查数据准确性' },
    { id: 502, groupId: 5, ruleName: '事实核查', riskLevel: 'high', ruleDescription: '核查事实准确性' }
])

const activeCategories = ref([])
const selectedRules = ref([])
const selectedAllInCategory = ref({})

// Initialize selectedAllInCategory
ruleCategories.value.forEach(cat => {
    selectedAllInCategory.value[cat.id] = false
})

// Watch selected rules to update "Select All" checkboxes
watch(selectedRules, (newVal) => {
    ruleCategories.value.forEach(cat => {
        const categoryRules = getRulesByCategory(cat.id).map(r => r.id)
        const allSelected = categoryRules.length > 0 && 
                          categoryRules.every(id => newVal.includes(id))
        selectedAllInCategory.value[cat.id] = allSelected
    })
}, { deep: true })

// Methods for rule selection
const getRulesByCategory = (categoryId) => {
    return allRules.value.filter(rule => rule.groupId === categoryId)
}

const toggleSelectAllCategory = (categoryId, selected) => {
    const categoryRuleIds = getRulesByCategory(categoryId).map(r => r.id)
    
    if (selected) {
        // Add all category rules (avoid duplicates)
        const newSelected = [...new Set([...selectedRules.value, ...categoryRuleIds])]
        selectedRules.value = newSelected
    } else {
        // Remove all category rules
        selectedRules.value = selectedRules.value.filter(id => !categoryRuleIds.includes(id))
    }
}

const startAudit = () => {
    if (selectedRules.value.length === 0) {
        ElMessage.warning('请至少选择一条审核规则')
        return
    }
    activeStep.value = 2
}

const backToSelection = () => {
    activeStep.value = 1
}

// Audit results data and methods
const filterRiskLevels = ref([])
const activeNames = ref([])

// Original example data
const auditResults = ref([
    {
        "checkDetails": "检查文字的拼写是否正确，有无形似字、同音字误用等情况。",
        "id": 1,
        "checkItem": "错别字",
        "riskLevel": "high",
        "type": "语言规范",
        "context": "示例文本：'请确任您的信息正确无误'中的'确任'应为'确认'",
        "suggestion": "建议使用拼写检查工具全面检查文档",
        "position": { "page": 1, "line": 5, "column": 10 }
    },
    {
        "checkDetails": "查看标点符号的使用是否准确，是否存在标点缺失、多余或使用不当的问题，如该用句号的地方用了逗号，引号的使用是否规范等。",
        "id": 2,
        "checkItem": "标点符号",
        "riskLevel": "medium",
        "type": "语言规范",
        "context": "示例文本：'他说'你好吗？''中的引号使用不规范",
        "suggestion": "建议统一使用中文标点符号规范",
        "position": { "page": 2, "line": 3, "column": 15 }
    },
    {
        "checkDetails": "确保句子表达流畅，没有语病，如主谓宾搭配不当、成分残缺、语序失调、句式杂糅等问题。",
        "id": 3,
        "checkItem": "语句通顺",
        "riskLevel": "high",
        "type": "语言规范",
        "context": "示例文本：'通过这次学习，使我提高了认识'存在主语缺失问题",
        "suggestion": "建议修改为'这次学习使我提高了认识'",
        "position": { "page": 1, "line": 8, "column": 20 }
    },
    {
        "checkDetails": "检查内容的表述是否逻辑清晰、严密，避免出现前后矛盾、概念模糊、以偏概全、偷换概念等问题。",
        "id": 4,
        "checkItem": "表达严谨",
        "riskLevel": "high",
        "type": "逻辑结构",
        "context": "示例文本：'所有人都同意这个观点'与后文'部分人持反对意见'矛盾",
        "suggestion": "建议修改为'大多数人同意这个观点'",
        "position": { "page": 3, "line": 12, "column": 5 }
    },
    {
        "checkDetails": "确认文章符合相应语言的语法规则，包括词性使用、句子结构、时态语态等方面的正确性。",
        "id": 5,
        "checkItem": "语法规范",
        "riskLevel": "high",
        "type": "语言规范",
        "context": "示例文本：'他昨天去公园了，看到很多美丽的花儿'时态不一致",
        "suggestion": "建议统一使用过去时态",
        "position": { "page": 2, "line": 7, "column": 8 }
    },
    {
        "checkDetails": "检查用词是否精准、合适，是否存在用词不当、词义重复、词语搭配不合理等问题，同时要注意词语的感情色彩和语体色彩是否符合文章的风格和语境。",
        "id": 6,
        "checkItem": "用词恰当",
        "riskLevel": "medium",
        "type": "语言规范",
        "context": "示例文本：'这个方案非常牛逼'在正式文档中用语不当",
        "suggestion": "建议修改为'这个方案非常出色'",
        "position": { "page": 4, "line": 9, "column": 12 }
    },
    {
        "checkDetails": "查看文章是否涵盖了需要表达的所有要点，有无重要信息遗漏，开头、中间和结尾是否完整，是否存在虎头蛇尾或结构松散的情况。",
        "id": 7,
        "checkItem": "内容完整",
        "riskLevel": "high",
        "type": "逻辑结构",
        "context": "文档缺少结论部分",
        "suggestion": "建议补充结论章节",
        "position": { "page": 5, "line": 0, "column": 0 }
    },
    {
        "checkDetails": "如果有特定的格式要求，如字体、字号、行距、段落缩进、页码设置等，要检查是否符合规范。",
        "id": 8,
        "checkItem": "格式规范",
        "riskLevel": "low",
        "type": "格式规范",
        "context": "正文部分使用了多种字体",
        "suggestion": "建议统一使用宋体小四号字",
        "position": { "page": 1, "line": 1, "column": 1 }
    },
    {
        "checkDetails": "若文章中有引用他人的观点、数据、研究成果等，要检查引用格式是否正确，是否注明了出处，有无抄袭、剽窃等学术不端行为。",
        "id": 9,
        "checkItem": "引用规范",
        "riskLevel": "high",
        "type": "学术规范",
        "context": "引用了2023年统计数据但未注明来源",
        "suggestion": "建议补充引用来源",
        "position": { "page": 3, "line": 15, "column": 22 }
    },
    {
        "checkDetails": "检查文章整体的语言风格是否一致，如正式与非正式、书面语与口语等风格不能混杂，以免造成阅读上的突兀感。",
        "id": 10,
        "checkItem": "风格统一",
        "riskLevel": "medium",
        "type": "语言规范",
        "context": "文中既有'综上所述'等正式用语，又有'说白了'等口语表达",
        "suggestion": "建议统一使用正式语体",
        "position": { "page": 2, "line": 18, "column": 7 }
    },
    {
        "checkDetails": "对于包含数据的文章，要核实数据的真实性和准确性，确保数据来源可靠，引用无误，计算正确。",
        "id": 11,
        "checkItem": "数据准确性",
        "riskLevel": "high",
        "type": "内容真实",
        "context": "文中提到'市场占有率达到95%'但未提供数据来源",
        "suggestion": "建议补充数据来源或核实数据准确性",
        "position": { "page": 4, "line": 6, "column": 30 }
    },
    {
        "checkDetails": "检查文章中成语的使用是否正确，包括成语的书写、含义、语境搭配等方面。",
        "id": 12,
        "checkItem": "成语检查",
        "riskLevel": "medium",
        "type": "语言规范",
        "context": "示例文本：'他做事总是首当其冲'使用不当",
        "suggestion": "建议修改为'他做事总是冲在最前面'",
        "position": { "page": 3, "line": 9, "column": 14 }
    },
    {
        "checkDetails": "查看文章的风格是否与所描述的场景相契合，避免出现风格与场景不符的情况。",
        "id": 13,
        "checkItem": "场景风格检查",
        "riskLevel": "medium",
        "type": "语言规范",
        "context": "学术论文中使用了大量网络流行语",
        "suggestion": "建议使用规范的学术用语",
        "position": { "page": 5, "line": 11, "column": 18 }
    }
])

// Group results by type
const groupedResults = computed(() => {
    const groups = {}
    auditResults.value.forEach(rule => {
        if (!groups[rule.type]) {
            groups[rule.type] = []
        }
        groups[rule.type].push(rule)
    })
    return groups
})

// Filtered results
const filteredResults = computed(() => {
    const result = {}
    for (const [type, items] of Object.entries(groupedResults.value)) {
        const filteredItems = items.filter(item => {
            if (filterRiskLevels.value.length > 0 && !filterRiskLevels.value.includes(item.riskLevel)) {
                return false
            }
            return true
        })
        if (filteredItems.length > 0) {
            result[type] = filteredItems
        }
    }
    return result
})

// Helper methods
const formatRiskLevel = (level) => {
    const map = { high: '高风险', medium: '中风险', low: '低风险' }
    return map[level] || level
}

const getRiskTagType = (level) => {
    const map = { high: 'danger', medium: 'warning', low: 'success' }
    return map[level] || ''
}

const getHighestRiskLevel = (results) => {
    if (results.some(item => item.riskLevel === 'high')) return 'high'
    if (results.some(item => item.riskLevel === 'medium')) return 'medium'
    return 'low'
}

const countRiskLevel = (results, level) => {
    return results.filter(item => item.riskLevel === level).length
}

const hasRiskLevel = (results, level) => {
    return results.some(item => item.riskLevel === level)
}

const navigateToPosition = (position) => {
    ElMessage.success(`定位到第${position.page}页第${position.line}行第${position.column}列`)
}

const exportResults = () => {
    const data = JSON.stringify(filteredResults.value, null, 2)
    const blob = new Blob([data], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `文档审核结果_${new Date().toISOString().slice(0, 10)}.json`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
}
</script>

<style lang="scss" scoped>
.ai-audit-container {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
}

/* Rule selection styles */
.rule-selection {
    background: #fff;
    padding: 0px;
    border-radius: 8px;
    
    .selection-header {
        margin-bottom: 10px;
        
        h2 {
            margin: 0 0 8px 0;
            color: #333;
        }
        
        p {
            margin: 0;
            color: #666;
            font-size: 14px;
        }
    }
    
    .category-item {
        margin-bottom: 10px;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        overflow: hidden;
        
        :deep(.el-collapse-item__header) {
            padding: 12px 16px;
            background-color: #f5f7fa;
            border-bottom: none;
        }
        
        :deep(.el-collapse-item__wrap) {
            border-bottom: none;
        }
        
        :deep(.el-collapse-item__content) {
            padding: 0;
        }
        
        .category-header {
            display: flex;
            align-items: center;
            gap: 10px;
            flex: 1;
            
            // span {
            //     flex: 1;
            // }
            
            .el-checkbox {
                margin-left: auto;
            }
        }
        
        .rule-list {
            padding: 10px;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        
        .rule-item {
            margin: 0;
            padding: 10px;
            border-radius: 4px;
            border: 1px solid #ebeef5;
            transition: all 0.3s;
            height: 70px ; 
            
            &:hover {
                border-color: #409eff;
                background-color: #f5f7fa;
            }
            
            :deep(.el-checkbox__label) {
                display: flex;
                width: 100%;
            }
            
            .rule-content {
                flex: 1;
                
                .rule-header {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    margin-bottom: 6px;
                    
                    .rule-name {
                        font-weight: 500;
                        flex: 1;
                    }
                }
                
                .rule-desc {
                    margin: 0;
                    color: #666;
                    font-size: 13px;
                    line-height: 1.5;
                }
            }
        }
    }
    
    .action-buttons {
        display: flex;
        justify-content: center;
        margin-top: 20px;
        
        .el-button {
            width: 100%;
        }
    }
}

/* Audit results styles */
.audit-results-view {
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        h1 {
            margin: 0;
            color: #333;
        }
    }
    
    .filter-section {
        margin-bottom: 20px;
        display: flex;
        flex-direction: column;
        gap: 15px;
        
        .filter-group {
            display: flex;
            align-items: center;
            
            .filter-label {
                margin-right: 10px;
                font-size: 14px;
                color: #606266;
                min-width: 70px;
            }
            
            .el-checkbox-group {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
            }
        }
    }
    
    .audit-results {
        .collapse-title {
            display: flex;
            align-items: center;
            gap: 10px;
            flex: 1;
            
            .risk-tags {
                display: flex;
                gap: 8px;
                margin-left: auto;
            }
        }
        
        .audit-item {
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 4px;
            background-color: #f9f9f9;
            border-left: 4px solid #e0e0e0;
            
            &:hover {
                background-color: #f0f0f0;
            }
            
            .item-header {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
                gap: 10px;
                
                .item-title {
                    font-weight: bold;
                    flex: 1;
                }
            }
            
            .item-content {
                h4 {
                    margin: 10px 0 5px;
                    color: #666;
                }
                
                p, pre {
                    margin: 0 0 10px;
                    line-height: 1.5;
                }
                
                .problem-context {
                    margin-top: 10px;
                    padding: 10px;
                    background-color: #fff;
                    border-radius: 4px;
                    border: 1px solid #eee;
                    
                    pre {
                        white-space: pre-wrap;
                        font-family: inherit;
                        margin-top: 5px;
                    }
                }
            }
        }
        
        .audit-item[data-risk="high"] {
            border-left-color: #f56c6c;
        }
        
        .audit-item[data-risk="medium"] {
            border-left-color: #e6a23c;
        }
        
        .audit-item[data-risk="low"] {
            border-left-color: #67c23a;
        }
    }
}

.audit-step-container{
    max-width: 800px; 
    margin: 0 auto 10px;
    height:40px;
}

</style>

<style lang="css">
.audit-step-container .el-step.is-simple .el-step__head{
    display: flex;
}

.audit-step-container .el-step.is-simple .el-step__title{
    font-size: 15px;
}
</style>
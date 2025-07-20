<template>
    <div class="ai-audit-container">
        <div class="header">
            <h1>AI文档审核</h1>
            <div class="actions">
                <el-button type="primary" text bg icon="Download" @click="exportResults">导出审核结果</el-button>
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

        <el-scrollbar style="height:calc(100vh - 330px)">
            <div class="audit-results">

                <el-collapse v-model="activeNames" accordion expand-icon-position="left" @change="handleChange">
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
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Position } from '@element-plus/icons-vue'

// 审核规则数据
const auditRules = [
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
]

// 按类型分组的结果
const groupedResults = computed(() => {
    const groups = {}
    auditRules.forEach(rule => {
        if (!groups[rule.type]) {
            groups[rule.type] = []
        }
        groups[rule.type].push(rule)
    })
    return groups
})

// 过滤条件
// const filterRiskLevels = ref([])
// const filterType = ref('')

// 将原来的 filterType 改为 filterTypes 数组形式
const filterTypes = ref([])
const filterRiskLevels = ref([])

// 修改过滤逻辑
const filteredResults = computed(() => {
    const result = {}

    for (const [type, items] of Object.entries(groupedResults.value)) {
        // 类型过滤
        if (filterTypes.value.length > 0 && !filterTypes.value.includes(type)) continue

        const filteredItems = items.filter(item => {
            // 风险等级过滤
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

// 当前展开的面板
const activeNames = ref([])

// 格式化风险等级显示
const formatRiskLevel = (level) => {
    const map = {
        high: '高风险',
        medium: '中风险',
        low: '低风险'
    }
    return map[level] || level
}

// 获取风险等级对应的标签类型
const getRiskTagType = (level) => {
    const map = {
        high: 'danger',
        medium: 'warning',
        low: 'success'
    }
    return map[level] || ''
}

// 获取一组结果中的最高风险等级
const getHighestRiskLevel = (results) => {
    if (results.some(item => item.riskLevel === 'high')) return 'high'
    if (results.some(item => item.riskLevel === 'medium')) return 'medium'
    return 'low'
}

// 计算特定风险等级的数量
const countRiskLevel = (results, level) => {
    return results.filter(item => item.riskLevel === level).length
}

// 检查是否包含特定风险等级
const hasRiskLevel = (results, level) => {
    return results.some(item => item.riskLevel === level)
}

// // 过滤后的结果
// const filteredResults = computed(() => {
//   const result = {}

//   for (const [type, items] of Object.entries(groupedResults.value)) {
//     // 类型过滤
//     if (filterType.value && type !== filterType.value) continue

//     const filteredItems = items.filter(item => {
//       // 风险等级过滤
//       if (filterRiskLevels.value.length > 0 && !filterRiskLevels.value.includes(item.riskLevel)) {
//         return false
//       }
//       return true
//     })

//     if (filteredItems.length > 0) {
//       result[type] = filteredItems
//     }
//   }

//   return result
// })

// 定位到问题位置
const navigateToPosition = (position) => {
    // 实际项目中这里可以实现定位到文档具体位置
    ElMessage.success(`定位到第${position.page}页第${position.line}行第${position.column}列`)
    console.log('定位到:', position)

    // 模拟定位效果
    // 实际项目中可能需要滚动到文档对应位置或高亮显示问题文本
}

// 导出审核结果
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

    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;

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

                p,
                pre {
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

        // 根据风险等级设置边框颜色
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
</style>
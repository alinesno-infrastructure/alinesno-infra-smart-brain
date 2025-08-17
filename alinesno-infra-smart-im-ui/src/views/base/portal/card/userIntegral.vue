<template>
    <div class="user-integral">
        <div class="integral-title"> 
            <el-text class="mx-1" type="info">权益中心</el-text>

            <el-button size="default" type="info" text bg @click="showIntegral">服务</el-button>
        </div>
        <div class="integral-level" :class="userType" @click="displayIntegral" >
            <i :class="userIcon"></i>
            <span>{{ userTypeText }}</span>
        </div>

        <!-- 显示积分状态 -->
        <el-dialog
            v-model="dialogDisplayVisible"  
            title="查看组织积分余额和明细记"
            width="80%" 
            style="max-width:900px"
            :before-close="handleClose"
          >
           <IntegralDisplayPanel />
        </el-dialog>

        <!-- 显示积分规则 -->
        <el-dialog
            v-model="dialogVisible" 
            width="80%"
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            style="max-width:900px"
            :before-close="handleClose"
          >
            <el-collapse-transition>
            <div class="user-integral-package" v-if="showIntegralPanel">
                <div class="integral-price-title">
                    <span class="integral-price-label">价格</span>
                    <span class="integral-price-desc">
                    🚀选择智能体工作每天节省你的时间已有 平均处理速度提升<span class="highlight-number">3倍</span>以上,<span class="highlight-number">2.7个月</span>即可收回投资成本
                </span>
            </div>

                <el-row>
                    <el-col 
                        v-for="(packageItem, index) in packages" 
                        :key="index" 
                        :span="12"
                    >
                        <div class="package-container">
                            <div class="package-title">
                                {{ packageItem.name }}
                                <div class="package-price">{{ packageItem.price }}</div>
                                <div class="package-pay">
                                    <el-button 
                                        type="primary" 
                                        size="large" 
                                        @click="enterPay(packageItem)"
                                        style="width:100%"
                                    >
                                        升级{{ packageItem.name }}版本
                                    </el-button>
                                </div>
                            </div> 
                            
                            <ul class="package-features">
                                <li v-for="(feature, fIndex) in packageItem.features" :key="fIndex">
                                    <i :class="feature.icon"></i>
                                    <span v-html="formatFeatureText(feature.text)"></span>
                                    <el-link 
                                        v-if="feature.link" 
                                        href="#" 
                                        type="primary" 
                                        :underline="false"
                                    >
                                        了解更多
                                    </el-link>
                                </li>
                            </ul>
                        </div>
                    </el-col>
                </el-row>

               

            </div>

            <div v-if="!showIntegralPanel">
                <UserPay :packageItem="currentPackageItem" @goBack="enterPay" />
            </div>

            </el-collapse-transition>

             <template #footer>
                  <div class="dialog-footer"> 
                       <el-text class="mx-1" type="info">遇到问题?</el-text> &nbsp; 
                       <el-link type="info" href="#" :underline="false">联系我们获取帮助</el-link>
                  </div>
                </template>
             
          </el-dialog>

    </div>
</template>

<script setup>
import { computed } from 'vue';

import IntegralDisplayPanel from "./integralDisplay"
import UserPay from "./userPay"

const props = defineProps({
    // 用户类型: trial(试用), standard(标准), enterprise(企业)
    type: {
        type: String,
        default: 'trial',
        validator: (value) => ['trial', 'standard', 'enterprise'].includes(value)
    }
});

const showIntegralPanel = ref(true)
const currentPackageItem = ref(null)
const dialogVisible = ref(false)
const dialogDisplayVisible = ref(false)

// 用户类型对应文本
const userTypeText = computed(() => {
    const map = {
        trial: '试用用户',
        standard: '标准用户',
        enterprise: '企业用户'
    };
    return map[props.type] || '试用用户';
});

// 用户类型对应图标
const userIcon = computed(() => {
    const map = {
        trial: 'fas fa-user-clock',
        standard: 'fas fa-user-check',
        enterprise: 'fas fa-building-user'
    };
    return map[props.type] || 'fas fa-user-clock';
});

// 显示套餐积分
const showIntegral = () => {
    dialogVisible.value = true ;
    showIntegralPanel.value = true ;
}

// 套餐数据
const packages = ref([
    {
        name: 'AIP 基础版',
        version: 'Beta',
        price: '1000 元/月',
        features: [
            { icon: 'fas fa-coins', text: '每个月100000积分', link: true },
            { icon: 'fas fa-tasks', text: '最多可以同时运行2个长任务和5个对话' },
            { icon: 'fas fa-server', text: '专属资源提升稳定性' },
            { icon: 'fas fa-expand', text: '扩展的上下文长度' },
            { icon: 'fas fa-bolt', text: '高峰时段优先访问' }
        ]
    },
    {
        name: 'AIP 专业版',
        version: 'Beta',
        price: '3000 元/月',
        features: [
            { icon: 'fas fa-coins', text: '每个月300000积分', link: true },
            { icon: 'fas fa-tasks', text: '最多可以同时运行5个长任务和15个对话' },
            { icon: 'fas fa-server', text: '高级专属资源保证性能' },
            { icon: 'fas fa-expand', text: '超长上下文支持' },
            { icon: 'fas fa-bolt', text: '最高优先级访问' },
            { icon: 'fas fa-lock', text: '高级安全功能' }  
        ]
    }
]);

// 进入支付界面
const enterPay = (packageItem) => {
    showIntegralPanel.value = !showIntegralPanel.value ;
    currentPackageItem.value = packageItem;
}

// 格式化特性文本，突出显示数字
const formatFeatureText = (text) => {
    return text.replace(/(\d+)/g, '<span class="highlight-number"> $1 </span>');
};

const displayIntegral = () => {
    dialogDisplayVisible.value = true ;
}

</script>

<style lang="scss">
.user-integral {
    font-size: 14px;
    position: absolute;
    bottom: 0px;
    width: 220px;
    padding: 12px;
    border-radius: 8px;
    background-color: #f8f9fa;
    margin-bottom: 8px;

    .integral-title {
            margin-bottom: 10px;
    color: #444;
    display: flex;
    font-weight:bold;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    text-align: center;
    width: 100%;
    }
    
    .integral-level {
        display: flex;
        align-items: center;
        gap: 10px;
        cursor: pointer;
        border-radius: 6px;
        transition: all 0.3s ease;
        
        i {
            font-size: 16px;
            width: 20px;
            text-align: center;
        }
        
        span {
            font-weight: 500;
        }
        
        &.trial {
            color: #FF9500;
            background-color: rgba(255, 149, 0, 0.1);
            
            i {
                color: #FF9500;
            }
        }
        
        &.standard { 
            background-color: rgba(0, 122, 255, 0.1);
           
        }
        
        &.enterprise { 
            background-color: rgba(52, 199, 89, 0.1);
           
        }
    }

    .package-container {
        padding: 15px;
        margin: 15px 20px;
        border-radius: 8px;
        background-color: #fff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    
    .package-title {
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 5px;
        color: #333; 
        padding-bottom: 10px;

        .package-pay {
            margin-top: 15px;
        }
        
        .package-price {
            font-size: 18px; 
            margin-top: 5px;
            font-weight: bold;
        }
    }
    
    .package-features {
        list-style: none;
        padding: 0;
        margin: 0;
        
        li {
            display: flex;
            align-items: center;
            margin-bottom: 4px;
            font-size: 15px;
            color: #555;



            
            i {
                margin-right: 10px; 
                width: 20px;
                text-align: center;
            }
            
            .el-link {
                margin-left: 8px;
                font-size: 12px;
            }
        }
    }
}

            .highlight-number {
            font-weight: bold; 
            font-size: 1.1em;
        }

.integral-price-title{ 
    display: flex;
    flex-direction: column; 
    align-items: center; 
    margin-bottom: 15px;
    gap: 10px;

    .integral-price-label{
    font-size: 25px;
font-weight: bold;
    }
    
    .integral-price-desc{
        font-size: 14px;
}
}

.user-integral-package{
margin-top:10px;
    margin-right:30px;
    margin-left: 30px;
    margin-bottom: 0px;
}

.dialog-footer {
    margin-bottom: 10px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
}
}
</style>
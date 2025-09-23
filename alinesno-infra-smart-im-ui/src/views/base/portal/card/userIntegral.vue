<template>
    <div class="user-integral" v-if="openPoint">
        <div class="integral-title"> 
            <el-text class="mx-1" type="info">权益中心</el-text>

            <el-button size="default" type="info" text bg @click="showIntegral">服务</el-button>
        </div>
        <div class="integral-level" :class="userType" @click="displayIntegral" >
            <i :class="userIcon"></i>
            <span>{{ orgIntegral?.packageName }}</span>
        </div>

        <!-- 显示积分状态 -->
        <el-dialog
            v-model="dialogDisplayVisible"  
            title="查看组织积分余额和明细记"
            width="80%"
            fullscreen
            :before-close="handleClose"
          >
           <IntegralDisplayPanel ref="integralDisplayPanelRef" />
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
                                {{ packageItem.packageName }}
                                <div class="package-price">{{ packageItem.price }}元/月</div>
                                <div class="package-pay">

                                    <el-button 
                                        type="primary" 
                                        :loading="true"
                                        size="large" 
                                        v-if="payStatus === 'create_order' && currentPackageId == packageItem.id" 
                                        style="width:100%"
                                    >
                                      生成{{ packageItem.packageName }}订单中
                                    </el-button>

                                    <el-button 
                                        type="primary" 
                                        size="large"  
                                        :disabled="payStatus === 'create_order'"
                                        v-else
                                        @click="enterPay(packageItem)"
                                        style="width:100%"
                                    >
                                        升级{{ packageItem.packageName }}版本
                                    </el-button>
                                </div>
                            </div> 
                            
                            <ul class="package-features">
                                <li v-for="(feature, fIndex) in packageItem.featureList" :key="fIndex">
                                    <i :class="feature.icon"></i>
                                    <span v-html="formatFeatureText(feature.featureText)"></span>
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
                <UserPay :packageItem="currentPackageItem" :payQrcodeBase64="payQrcodeBase64" @goBack="closePayStatus" />
            </div>

            </el-collapse-transition>

            <!-- 添加验证状态提示 -->
            <div v-if="isVerifyingOrder" class="verifying-message">
                <el-icon class="is-loading"><Loading /></el-icon>
                <span>正在验证订单支付状态...</span>
            </div>

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
import { computed, nextTick } from 'vue';
import { ElMessage } from 'element-plus';

import IntegralDisplayPanel from "./integralDisplay"
import UserPay from "./userPay"

import {
    queryOrderWithQrcode,
    createOrder, 
    getIntegral,
    queryOrder,
    getPointsDetail,
    activePackages
} from "@/api/base/im/point"

const props = defineProps({
    // 用户类型: trial(试用), standard(标准), enterprise(企业)
    type: {
        type: String,
        default: 'trial',
        validator: (value) => ['trial', 'standard', 'enterprise'].includes(value)
    }
});

const openPoint = ref(false)
const currentPackageId = ref('')
const payStatus = ref('end')
const payQrcodeBase64 = ref('')
const showIntegralPanel = ref(true)
const currentPackageItem = ref(null)
const dialogVisible = ref(false)
const dialogDisplayVisible = ref(false)
const orgIntegral = ref(null);

const integralDisplayPanelRef = ref(null)

// 新增状态变量
const currentOrderId = ref(''); // 当前订单ID
const isVerifyingOrder = ref(false); // 是否正在验证订单
const orderCheckInterval = ref(null); // 订单检查定时器

// 用户类型对应文本
// const userTypeText = computed(() => {
//     const map = {
//         trial: '试用用户',
//         standard: '标准用户',
//         enterprise: '企业用户'
//     };
//     return map[props.type] || '试用用户';
// });

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

    activePackages().then(res => {
        packages.value = res.data;
    })
}

// 套餐数据
const packages = ref([]);

// 关闭支付二维码
const closePayStatus = () => {

    showIntegralPanel.value = !showIntegralPanel.value ;
    payStatus.value = 'end' ; 
    currentPackageId.value = '' ;  
    isVerifyingOrder.value = false ;

    // 返回时清除定时器
    clearOrderCheckInterval();
}

// 进入支付界面
const enterPay = (packageItem) => {

    payStatus.value = 'create_order' ; 
    currentPackageId.value = packageItem.id ;

    // 创建订单
    const data = {
        subject: packageItem.name + ':' + packageItem.version , 
        payType: 'wechatpay' , 
        payAmount: packageItem.price ,
        packageId: packageItem.id
    }

    createOrder(data).then(res => { 
        payStatus.value = 'end' ; 

        showIntegralPanel.value = !showIntegralPanel.value ;
        currentPackageItem.value = packageItem;  

        queryOrderWithQrcode(res.data , 'wechatpay').then(res => {
            payQrcodeBase64.value = 'data:image/png;base64,' + res.qrcodeBase64;

            payStatus.value = 'end' ; 
            currentPackageId.value = '' ;  
            currentOrderId.value = res.data.payOrderId;

            // 开始轮询订单状态
            startOrderStatusPolling();
        })

    }).catch(error => {
        console.log('error = ' +  error);
        payStatus.value = 'end' ; 
        currentPackageId.value = '' ; 
        ElMessage.error("订单生成失败..");
    })
}

// 开始轮询订单状态
const startOrderStatusPolling = () => {
    // 先清除已有的定时器
    clearOrderCheckInterval();
    
    // 立即检查一次订单状态
    checkOrderStatus();
    
    // 设置定时器，每5秒检查一次
    orderCheckInterval.value = setInterval(() => {
        checkOrderStatus();
    }, 2000);
}

// 检查订单状态
const checkOrderStatus = async () => {
    if (!currentOrderId.value) return;
    
    isVerifyingOrder.value = true;
    
    try {
        const response = await queryOrder(currentOrderId.value);
        const orderStatus = response.data.state; // 假设返回数据中有status字段
        
        if (orderStatus === 'success') {
            // 支付成功
            ElMessage.success('支付成功！');
            clearOrderCheckInterval();
            dialogVisible.value = false;
            
            // 刷新积分信息
            handleGetIntegral();
        } else if (orderStatus === 'failed' || orderStatus === 'closed') {
            // 支付失败或订单关闭
            ElMessage.warning(`订单状态: ${getPaymentStatusLabel(orderStatus)}`);
            clearOrderCheckInterval();
        }
        // 其他状态（waiting, refunded）继续轮询
    } catch (error) {
        console.error('查询订单状态失败:', error);
        ElMessage.error('查询订单状态失败');
    } finally {
        isVerifyingOrder.value = false;
    }
}

// 根据状态码获取状态标签
const getPaymentStatusLabel = (statusCode) => {
    const statusMap = {
        'waiting': '等待支付',
        'success': '支付成功',
        'failed': '支付失败',
        'closed': '订单已关闭',
        'refunded': '已退款'
    };
    return statusMap[statusCode] || '未知状态';
}

// 清除订单检查定时器
const clearOrderCheckInterval = () => {
    if (orderCheckInterval.value) {
        clearInterval(orderCheckInterval.value);
        orderCheckInterval.value = null;
    }
    isVerifyingOrder.value = false;
}

// 对话框关闭处理
const handleClose = (done) => {
    // 清除定时器
    clearOrderCheckInterval();
    done();
}

// 组件卸载时清除定时器
onUnmounted(() => {
    clearOrderCheckInterval();
});


// 格式化特性文本，突出显示数字
const formatFeatureText = (text) => {
    return text.replace(/(\d+)/g, '<span class="highlight-number"> $1 </span>');
};

const displayIntegral = () => {
    dialogDisplayVisible.value = true ;
    handleGetIntegral();
    
    nextTick(() => {
        integralDisplayPanelRef.value.setOrgIntegral(orgIntegral.value);
    });
}

// 获取用户积分
const handleGetIntegral = () => {
    getIntegral().then(res => { 
        console.log('res = ' +  res);
        openPoint.value = res.openPoint ;
        if(openPoint.value){
            orgIntegral.value = res.data;
        }
    }).catch(error => {
        console.log('error = ' +  error);
    })
}


handleGetIntegral();

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

    .verifying-message {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
        color: #409EFF;
        
        .el-icon {
            margin-right: 8px;
        }
    }

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
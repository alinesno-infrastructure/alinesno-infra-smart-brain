<template>
  <div class="pay-container">
    <div class="pay-modal">
      <div class="pay-header">
        <el-page-header @click="$emit('goBack')">
          <template #content>
            <span class="text-large font-600 mr-3"> <h3><i class="fa-brands fa-cc-amazon-pay"></i> 微信支付</h3> </span>
          </template>
        </el-page-header>
      </div>

      <div class="pay-content" v-if="packageItem">
        <div class="plan-info">
          <h4>{{ packageItem.packageName || '套餐名称' }}</h4>
          <p class="price">{{ packageItem.price + '元/月' || '0 元/月' }}</p>
          <ul class="features" v-if="packageItem.featureList">
            <li v-for="(feature, index) in packageItem.featureList" :key="index">
              <i :class="feature.icon"></i>
              <span v-html="formatFeatureText(feature.featureText)"></span> 
            </li>
          </ul>
          <p class="more-info">了解更多</p>
        </div>
        
        <div class="qr-code-section">
          <div class="qr-code-placeholder">
            <div class="qr-code">
              <span v-if="!packageItem">加载中...</span>
              <img v-else :src="props.payQrcodeBase64" />
            </div>
            <p>请使用微信扫描二维码完成支付</p>
          </div>
          <div class="payment-amount">
            <span>支付金额</span>
            <span class="amount">{{ extractPrice(packageItem?.price) }}</span>
          </div>
        </div>
      </div>
      <div v-else class="loading-container">
        <el-icon class="is-loading" size="large"><Loading /></el-icon>
        <p>加载套餐信息中...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  packageItem: {
    type: Object,
    default: () => ({})
  }, 
  payQrcodeBase64: {
    type: String , 
    default: null
  }
});

defineEmits(['goBack']);

// 从价格文本中提取数字部分
const extractPrice = (priceText) => {
  console.log('priceText = ' + priceText)
  console.log('priceText type = ' + typeof priceText)

  if (!priceText && priceText !== 0) return '¥0.00';
  
  // 将输入转换为字符串
  const text = String(priceText);
  const match = text.match(/(\d+\.?\d*)/);
  return match ? `¥${match[0]}.00` : '¥0.00';
};

// 格式化特性文本，突出显示数字
const formatFeatureText = (text) => {
  if (!text) return '';
  return text.replace(/(\d+)/g, '<span class="highlight-number">$1</span>');
};
</script>

<style lang="scss" scoped>
.pay-container {  
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.pay-modal {
  width: 100%;
  background-color: #fff;
  border-radius: 8px;
  margin-right:40px;
  margin-left:40px;
  margin-top: 10px; 
  overflow: hidden; 
}

.pay-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px; 
  
  h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
  }
  
  .close-btn {
    font-size: 24px;
    cursor: pointer;
    color: #999;
    &:hover {
      color: #666;
    }
  }
}

.pay-content {
  display: flex;
  padding: 24px; 
}

.plan-info {
  flex: 1;
  padding-right: 24px;
  
  h4 {
    margin: 0 0 12px 0;
    font-size: 16px;
    color: #333;
    font-weight: bold;
  }
  
  .price {
    font-size: 20px;
    font-weight: bold;
    color: #f56c6c;
    margin: 0 0 10px 0;
  }
  
  .features {
        list-style: none;
        padding: 0;
        margin: 0;
        color: #555;
        
        li {
            display: flex;
            align-items: center;
            margin-bottom: 4px;
            font-size: 15px;
            
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
  
  .more-info {
    color: #1890ff;
    font-size: 14px;
    cursor: pointer;
    margin: 0;
  }
}

.qr-code-section {
  width: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .qr-code-placeholder {
    text-align: center;
    margin-bottom: 20px;
    
    .qr-code {
      width: 200px;
      height: 200px;
      background-color: #f5f5f5;
      margin: 0 auto 12px;
      display: flex;
      justify-content: center;
      align-items: center;
      border-radius: 5px;
      color: #999; 
      border: 1px solid #e5e5e5;

      img {
      	width:100%;
      	height: 100%;
      	border-radius: 5px;
      }
    }
    
    p {
      margin: 0;
      font-size: 14px;
      color: #666;
    }
  }
  
  .payment-amount {
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 12px;
    background-color: #f9f9f9;
    border-radius: 4px;
    
    span {
      font-size: 14px;
      color: #333;
    }
    
    .amount {
      font-weight: bold;
      color: #f56c6c;
    }
  }
}

.pay-footer {
  padding: 12px 24px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
  font-size: 14px;
  color: #999;
}
</style>
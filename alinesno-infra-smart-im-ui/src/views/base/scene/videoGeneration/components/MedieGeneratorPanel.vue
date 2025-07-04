<template>
  <div class="media-generator-container">
    <!-- 步骤1：生成故事角色 -->
    <section>
      <h2 class="step-title">1. 生成故事角色</h2>
      <div class="scroll-container">
        <div class="scroll-wrapper" ref="roleScroll" @scroll="updateIndicators('role')">
          <div class="card" v-for="(role, index) in roles" :key="'role'+index">
            <img :src="role.image" :alt="role.alt" class="card-img">
            <div class="card-content">
              <p class="card-desc">{{ role.description }}</p>
              <div class="card-actions">
                <el-button type="primary" text bg @click="regenerate('role', index)">重新生成</el-button>
                <el-button type="primary" text bg @click="edit('role', index)">编辑</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="scroll-btn scroll-left" @click="scrollLeft('role')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 18L9 12L15 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-btn scroll-right" @click="scrollRight('role')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-indicator">
          <div class="indicator-dot" 
               v-for="(dot, index) in roleIndicators" 
               :key="'role-dot'+index"
               :class="{active: dot.active}"
               @click="goToSlide('role', index)"></div>
        </div>
      </div>
    </section>

    <!-- 步骤2：生成分镜画面 -->
    <section>
      <h2 class="step-title">2. 生成分镜画面</h2>
      <div class="scroll-container">
        <div class="scroll-wrapper" ref="sceneScroll" @scroll="updateIndicators('scene')">
          <div class="card" v-for="(scene, index) in scenes" :key="'scene'+index">
            <img :src="scene.image" :alt="scene.alt" class="card-img">
            <div class="card-content">
              <p class="card-desc">{{ scene.description }}</p>
              <div class="card-actions">
                <el-button type="primary" text bg  @click="regenerate('scene', index)">重新生成</el-button>
                <el-button type="primary" text bg  @click="edit('scene', index)">编辑</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="scroll-btn scroll-left" @click="scrollLeft('scene')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 18L9 12L15 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-btn scroll-right" @click="scrollRight('scene')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-indicator">
          <div class="indicator-dot" 
               v-for="(dot, index) in sceneIndicators" 
               :key="'scene-dot'+index"
               :class="{active: dot.active}"
               @click="goToSlide('scene', index)"></div>
        </div>
      </div>
    </section>

    <!-- 步骤3：生成分镜视频 -->
    <section>
      <h2 class="step-title">3. 生成分镜视频</h2>
      <div class="scroll-container">
        <div class="scroll-wrapper" ref="videoScroll" @scroll="updateIndicators('video')">
          <div class="card video-card" v-for="(video, index) in videos" :key="'video'+index">
            <img :src="video.image" :alt="video.alt" class="video-placeholder">
            <span class="video-duration">{{ video.duration }}</span>
            <div class="card-content">
              <p class="card-desc">{{ video.description }}</p>
              <div class="card-actions">
                <el-button type="primary" text bg  @click="regenerate('video', index)">重新生成</el-button>
                <el-button type="primary" text bg  @click="edit('video', index)">编辑</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="scroll-btn scroll-left" @click="scrollLeft('video')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 18L9 12L15 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-btn scroll-right" @click="scrollRight('video')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-indicator">
          <div class="indicator-dot" 
               v-for="(dot, index) in videoIndicators" 
               :key="'video-dot'+index"
               :class="{active: dot.active}"
               @click="goToSlide('video', index)"></div>
        </div>
      </div>
    </section>

    <!-- 步骤4：生成分镜配音 -->
    <section>
      <h2 class="step-title">4. 生成分镜配音</h2>
      <div class="scroll-container">
        <div class="scroll-wrapper" ref="audioScroll" @scroll="updateIndicators('audio')">
          <div class="card video-card" v-for="(audio, index) in audios" :key="'audio'+index">
            <img :src="audio.image" :alt="audio.alt" class="video-placeholder">
            <span class="video-duration">{{ audio.duration }}</span>
            <div class="card-content">
              <p class="card-desc">{{ audio.description }}</p>
              <div class="card-actions">
                <el-button type="primary" text bg  @click="regenerate('audio', index)">重新生成</el-button>
                <el-button type="primary" text bg  @click="edit('audio', index)">编辑</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="scroll-btn scroll-left" @click="scrollLeft('audio')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 18L9 12L15 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-btn scroll-right" @click="scrollRight('audio')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 18L15 12L9 6" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="scroll-indicator">
          <div class="indicator-dot" 
               v-for="(dot, index) in audioIndicators" 
               :key="'audio-dot'+index"
               :class="{active: dot.active}"
               @click="goToSlide('audio', index)"></div>
        </div>
      </div>
    </section>

    <!-- 步骤5：视频剪辑 + 步骤6：最终视频 -->
    <section class="final-video">
      <div>
        <h2 class="step-title">5. 视频剪辑</h2> <br/>
        <h2 class="step-title" style="margin-top: 10px;">6. 最终视频</h2>
      </div>
      <img src="https://picsum.photos/id/3/800/600" alt="最终视频预览">
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 数据定义
const roles = ref([
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '故事角色1',
    description: '角色描述：小兔子，耳朵长长的，红眼睛。服饰：蓝色睡衣（卧室电视机前）'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '故事角色4',
    description: '角色描述：卧室背景，有床和窗户。'
  }
]);

const scenes = ref([
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜画面1',
    description: '首帧描述：卡通风格插图，卧室里电视机前，一只耳朵长长的、红眼睛的小兔子...'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜画面2',
    description: '首帧描述：卡通风格插图，卧室电视机前，面容和蔼的兔妈妈穿着粉色围裙，走向坐...'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜画面3',
    description: '首帧描述：卡通风格插图，卧室电视机前，耳朵长长的、红眼睛的小兔子穿着蓝色睡...'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜画面4',
    description: '首帧描述：中景，兔妈妈站在小兔子身边，手指向时钟。'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜画面5',
    description: '首帧描述：近景，小兔子皱着眉头，摇头拒绝。'
  }
]);

const videos = ref([
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '视频画面1',
    duration: '00:00 / 00:05',
    description: '描述：近景，小兔子，坐在电视机前，眼睛专注地看着电视节目。'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '视频画面2',
    duration: '00:00 / 00:05',
    description: '描述：中景，兔妈妈，从旁边走来，站在小兔子身边。'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '视频画面3',
    duration: '00:00 / 00:05',
    description: '描述：近景，小兔子，眼睛盯着电视，脑袋左右晃动表示拒绝。'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '视频画面4',
    duration: '00:00 / 00:05',
    description: '描述：中景，兔妈妈手指向墙上的时钟。'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '视频画面5',
    duration: '00:00 / 00:05',
    description: '描述：近景，小兔子皱着眉头，不情愿地摇头。'
  }
]);

const audios = ref([
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜配音1',
    duration: '00:00 / 00:02',
    description: '“这个节目真好看。”'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜配音2',
    duration: '00:00 / 00:00',
    description: '“宝贝呀，该睡觉咯。”'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜配音3',
    duration: '00:00 / 00:00',
    description: '“妈妈，再看一会儿嘛。”'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜配音4',
    duration: '00:00 / 00:00',
    description: '“你看看都几点了，明天还要上学呢。”'
  },
  {
    image: 'https://picsum.photos/id/3/800/600',
    alt: '分镜配音5',
    duration: '00:00 / 00:00',
    description: '“不嘛不嘛，我就要再看一会儿。”'
  }
]);

// 指示器状态
const roleIndicators = ref(Array(roles.value.length).fill().map((_, i) => ({ active: i === 0 })));
const sceneIndicators = ref(Array(scenes.value.length).fill().map((_, i) => ({ active: i === 0 })));
const videoIndicators = ref(Array(videos.value.length).fill().map((_, i) => ({ active: i === 0 })));
const audioIndicators = ref(Array(audios.value.length).fill().map((_, i) => ({ active: i === 0 })));

// DOM 引用
const roleScroll = ref(null);
const sceneScroll = ref(null);
const videoScroll = ref(null);
const audioScroll = ref(null);

// 滚动控制函数
function scrollLeft(type) {
  const container = getScrollContainer(type);
  container.scrollBy({ left: -320, behavior: 'smooth' });
}

function scrollRight(type) {
  const container = getScrollContainer(type);
  container.scrollBy({ left: 320, behavior: 'smooth' });
}

function goToSlide(type, slideIndex) {
  const container = getScrollContainer(type);
  const slideWidth = 320; // 卡片宽度 + 间距
  container.scrollTo({ left: slideIndex * slideWidth, behavior: 'smooth' });
}

function updateIndicators(type) {
  const container = getScrollContainer(type);
  const indicators = getIndicators(type);
  const scrollPosition = container.scrollLeft;
  const slideWidth = 320; // 卡片宽度 + 间距
  const currentSlide = Math.round(scrollPosition / slideWidth);
  
  // 更新指示器状态
  indicators.value.forEach((dot, i) => {
    dot.active = i === currentSlide;
  });
  
  // 控制滚动按钮显示/隐藏
  const parent = container.parentElement;
  const leftBtn = parent.querySelector('.scroll-left');
  const rightBtn = parent.querySelector('.scroll-right');
  
  if (scrollPosition <= 10) {
    leftBtn.style.opacity = '0.3';
    leftBtn.style.pointerEvents = 'none';
  } else {
    leftBtn.style.opacity = '0.7';
    leftBtn.style.pointerEvents = 'auto';
  }
  
  const maxScroll = container.scrollWidth - container.clientWidth;
  if (scrollPosition >= maxScroll - 10) {
    rightBtn.style.opacity = '0.3';
    rightBtn.style.pointerEvents = 'none';
  } else {
    rightBtn.style.opacity = '0.7';
    rightBtn.style.pointerEvents = 'auto';
  }
}

// 辅助函数
function getScrollContainer(type) {
  switch(type) {
    case 'role': return roleScroll.value;
    case 'scene': return sceneScroll.value;
    case 'video': return videoScroll.value;
    case 'audio': return audioScroll.value;
    default: return null;
  }
}

function getIndicators(type) {
  switch(type) {
    case 'role': return roleIndicators;
    case 'scene': return sceneIndicators;
    case 'video': return videoIndicators;
    case 'audio': return audioIndicators;
    default: return null;
  }
}

// 操作函数
function regenerate(type, index) {
  console.log(`重新生成 ${type} 的第 ${index + 1} 项`);
  // 这里可以添加实际的重新生成逻辑
}

function edit(type, index) {
  console.log(`编辑 ${type} 的第 ${index + 1} 项`);
  // 这里可以添加实际的编辑逻辑
}

// 初始化
onMounted(() => {
  // 为所有滚动容器添加初始指示器状态
  updateIndicators('role');
  updateIndicators('scene');
  updateIndicators('video');
  updateIndicators('audio');
});
</script>

<style lang="scss" scoped>
.media-generator-container {

    width: 100%;
    padding: 30px;
    background: #fafafa;
    border-radius: 5px;
  
  /* 步骤标题样式 */
  .step-title {
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 20px;
    background-color: #fff;
    padding: 10px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    display: inline-block;
    margin: 0 auto 20px; 
  }
  
  /* 横向滚动容器 */
  .scroll-container {
    position: relative;
    margin-bottom: 40px;
  }
  
  .scroll-wrapper {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none; /* Firefox */
    display: flex;
    gap: 20px;
    padding-bottom: 15px;
    justify-content: center;
    
    &::-webkit-scrollbar {
      display: none; /* Chrome, Safari, Edge */
    }
  }
  
  /* 卡片样式 */
  .card {
    width: 300px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0,0,0,0.08);
    overflow: hidden;
    transition: transform 0.2s ease;
    flex-shrink: 0;
    
    &:hover {
      transform: translateY(-3px);
    }
  }
  
  .card-img {
    width: 100%;
    height: auto;
    display: block;
  }
  
  .card-content {
    padding: 16px;
  }
  
  .card-desc {
    font-size: 14px;
    color: #666;
    height: 50px;
    margin-bottom: 12px;
  }
  
  .card-actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  
  /* 视频预览特殊处理 */
  .video-card {
    position: relative;
  }
  
  .video-placeholder {
    width: 100%;
    height: auto;
    display: block;
  }
  
  .video-duration {
    position: absolute;
    bottom: 8px;
    left: 8px;
    background-color: rgba(0,0,0,0.6);
    color: #fff;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
  }
  
  /* 最终视频区域 */
  .final-video {
    text-align: center;
    margin-top: 40px;
    
    img {
      max-width: 100%;
      border-radius: 8px;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
  }
  
  /* 滚动控制按钮 */
  .scroll-btn {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 40px;
    height: 40px;
    background-color: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    cursor: pointer;
    z-index: 10;
    opacity: 0.7;
    transition: opacity 0.2s ease;
    
    &:hover {
      opacity: 1;
    }
  }
  
  .scroll-left {
    left: -15px;
  }
  
  .scroll-right {
    right: -15px;
  }
  
  /* 滚动指示器 */
  .scroll-indicator {
    display: flex;
    gap: 5px;
    justify-content: center;
    margin-top: 15px;
  }
  
  .indicator-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #ddd;
    transition: background-color 0.2s ease;
    cursor: pointer;
    
    &.active {
      background-color: #666;
    }
  }
  
  /* 移动端适配 */
  @media (max-width: 768px) {
    .scroll-btn {
      display: none;
    }
  }
}
</style>
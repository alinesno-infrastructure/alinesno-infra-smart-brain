<!-- 文档管理界面 -->
<template>
  <ContentFormatterContainer>
    <div class="document-manager">
      <!-- 筛选工具栏 -->
      <div class="filter-toolbar">
        <div class="toolbar-content">
          <div class="left-section">
            <h2>全部文档</h2>
            <span class="document-count">共 {{ total }} 个文档</span>
          </div>
          
          <div class="right-section">
            <div class="sort-dropdown">
              <select v-model="sortOption" class="sort-select">
                <option value="modified">最近修改时间</option>
                <option value="created">创建时间</option>
                <option value="name-asc">名称 (A-Z)</option>
                <option value="name-desc">名称 (Z-A)</option>
              </select>
              <i class="fas fa-chevron-down"></i>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 文档列表 -->
      <div class="document-grid">
        <div 
          v-for="doc in documentList" 
          :key="doc.id" 
          class="document-card"
          @mouseenter="hoverCard = doc.id"
          @mouseleave="hoverCard = null"
        >
          <div class="card-content">
            <h3 @click="handleEnterDocument(doc)" class="document-title">{{ doc.documentName }}</h3>
            <p class="document-description">{{ doc.documentDesc? doc.documentDesc :'暂无编辑内容'}}</p>
            <div class="card-footer">
              <span class="modified-date">{{ parseTime(doc.lastSavedTime) }}</span>
              <el-button 
                text 
                bg
                @click.stop="toggleFavorite(doc.id)"
                :class="{ favorited: doc.favorited }"
              >
                <i :class="doc.favorited ? 'fas fa-star' : 'far fa-star'"></i>
              </el-button>
            </div>
          </div>
        </div>


      </div>
        <div v-if="documentList.length === 0">
          <el-empty description="暂无文档，你可以通过新建立文档来添加">
            <el-button @click="handleCreateNewDocument()" text bg icon="Plus" type="primary">建立新文档</el-button>
          </el-empty>
        </div>
    </div>
  </ContentFormatterContainer>  
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ContentFormatterContainer from './common/ContentFormatterContainer'

import { 
  createNewDocument,
  listDocument,
} from '@/api/base/im/scene/contentFormatterDocument';

import SnowflakeId from "snowflake-id";
const snowflake = new SnowflakeId();

const { proxy } = getCurrentInstance()
const loading = ref(false)
const documentList = ref([])
const total = ref(0)
const dateRange = ref([]);

const router = useRouter();
const route = useRoute();

// 文档数据
const documents = ref([])
const sceneId = ref(route.query.sceneId)

const data = reactive({
  form: {
  },
  queryParams: {
    pageNum: 1,
    pageSize: 20,
  },
  rules: {
  }
});

const { queryParams, form, rules, chainForm, chainRules } = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listDocument(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    documentList.value = res.rows || [];
    total.value = res.total;
  });
};

const sortOption = ref('modified')
const activeMenu = ref(null)
const hoverCard = ref(null)

// 切换收藏状态
const toggleFavorite = (id) => {
  const doc = documents.value.find(d => d.id === id)
  if (doc) {
    doc.favorited = !doc.favorited
  }
}

// // 切换菜单显示
// const toggleMenu = (id) => {
//   activeMenu.value = activeMenu.value === id ? null : id
// }

// 关闭菜单
const closeMenu = () => {
  activeMenu.value = null
}

const handleCreateNewDocument = () => {
    createNewDocument(sceneId.value).then(res => {
        let documentId = res.data ;
        router.push({
            path: '/scene/contentFormatter/contentParser',
            query: {
                sceneId: sceneId.value,
                documentId: documentId ,
                channelStreamId: snowflake.generate() 
            }
        })
    })
}

const handleEnterDocument = (doc) => {
    router.push({
        path: '/scene/contentFormatter/contentParser',
        query: {
            sceneId: sceneId.value,
            documentId: doc.id ,
            channelStreamId: snowflake.generate() 
        }
    })
} 

onMounted(() => {
  getList()
})

</script>

<style lang="scss" scoped>
.document-manager {
  padding: 20px;
  color: #1F2937;
}

.filter-toolbar {
  background: #fafafa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  
  .toolbar-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .left-section {
    display: flex;
    align-items: center;
    gap: 8px;
    
    h2 {
      font-size: 18px;
      font-weight: 600;
      margin: 0;
    }
    
    .document-count {
      font-size: 14px;
      color: #6B7280;
      background: #F3F4F6;
      padding: 4px 8px;
      border-radius: 9999px;
    }
  }
  
  .right-section {
    display: flex;
    gap: 12px;
  }
  
  .sort-dropdown {
    position: relative;
    
    .sort-select {
      appearance: none;
      background: #F9FAFB;
      border: 1px solid #E5E7EB;
      color: #6B7280;
      border-radius: 8px;
      padding: 8px 32px 8px 16px;
      font-size: 14px;
      outline: none;
      cursor: pointer;
      
      &:focus {
        outline: 2px solid rgba(22, 93, 255, 0.3);
      }
    }
    
    .fa-chevron-down {
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 12px;
      color: #6B7280;
      pointer-events: none;
    }
  }
}

.document-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.document-card {
  background: white;
  border-radius: 12px;
  border: 1px solid rgba(6, 7, 9, 0.1);
  transition: all 0.3s;
  
  .card-content {
    padding: 16px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
    cursor: pointer;
  }
  
  .file-icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    
    &.file-type-word {
      background: rgba(22, 93, 255, 0.1);
      color: #165DFF;
    }
    
    &.file-type-pdf {
      background: rgba(245, 158, 11, 0.1);
      color: #F59E0B;
    }
    
    &.file-type-excel {
      background: rgba(16, 185, 129, 0.1);
      color: #10B981;
    }
  }
  
  .card-menu {
    position: relative;
    
    .menu-button {
      color: #6B7280;
      padding: 4px;
      
      &:hover {
        color: #1F2937;
      }
    }
    
  }
  
  .document-title {
    font-weight: bold;
    margin-top: 10px;
    font-size: 16px;
    white-space: nowrap;
    overflow: hidden;
    cursor: pointer;
    text-overflow: ellipsis;
  }
  
  .document-description {
    color: #6B7280;
    font-size: 14px;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: #6B7280;
    
    .favorite-button {
      color: #6B7280;
      transition: color 0.2s;
      
      &:hover {
        color: #165DFF;
      }
      
      &.favorited {
        color: #165DFF;
      }
    }
  }
}
</style>
<template>
   <div class="app-container memory-display-container">


    <div class="page-header-container" style="margin-bottom: 20px;">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="text-large font-600 mr-3"> 
               [业务专家]记忆库 
            </span>
            <span style="color: #aaaaaa;font-size: 14px;">更新时间：2025-02-14 23:50:44</span>
          </div>
        </template>
      </el-page-header>
   </div>

      <el-row :gutter="20">
         <el-col :span="24">
            <div class="grid-content">
               <div class="panel-header">
                  <div class="header-title">
                     <i class="fa-solid fa-brain"></i> 时间记忆热力图
                  </div>
                  <!--
                  <div class="header-desc">
                     根据每天产生的记数据，生成一个热力图，标识这个记忆库的更新状态和对应的内容
                  </div>
                  -->
               </div>
               <div class="panel-body acp-height-auto">
                  <div class="box-header">
                     <el-row>
                        <el-col :span="24">
                           <div class="sidecard-bar">
                              <div id="echarts-bar2-chart" :style="{ width: '100%', height: '200px' }"></div>
                           </div>
                        </el-col>
                     </el-row>
                  </div>
                  <div class="body-desc">
                     记忆贡献度的统计数据包括事件提交、创建任务、更新、合并等会被统计。
                  </div>
               </div>
            </div>
         </el-col>

         <el-col :span="12">
            <div class="grid-content">
               <div class="panel-body acp-height-auto">
                  <div class="panel-header">
                     <div class="header-title">
                        <i class="fa-solid fa-brain"></i> 记忆词云图
                     </div>
                  </div>
                  <div class="box-header">
                     <div class="sidecard-bar">
                        <div id="echarts-cloudword-chart" :style="{ width: '100%', height: '450px' }"></div>
                     </div>
                  </div>
                  <div class="body-desc">
                     记忆贡献度的统计数据包括事件提交、创建任务、更新、合并等会被统计。
                  </div>
               </div>
            </div>
         </el-col>

         <el-col :span="12">
            <div class="grid-content">
               <div class="panel-body acp-height-auto">
                  <div class="panel-header">
                     <div class="header-title">
                        <i class="fa-solid fa-brain"></i> 事件关联映射图 
                     </div>
                  </div>
                  <div class="box-header">
                     <div class="sidecard-bar">
                        <div id="echarts-entityMapper-chart" :style="{ width: '100%', height: '450px' }"></div>
                     </div>
                  </div>
                  <div class="body-desc">
                     由初始事件与对应的关系进行关联，从而形成关联图。
                  </div>
               </div>
            </div>
         </el-col>
      </el-row>

      <el-drawer v-model="drawer" title="2023-21-12事件记忆信息" size="50%" :before-close="handleClose">
         <el-table v-loading="loading" :data="MemoryList" @selection-change="handleSelectionChange">
            <el-table-column type="index" width="50" align="center" />

            <el-table-column label="时间" align="center" width="120px" prop="timestamp" v-if="columns[0].visible">
               <template #default="scope">
               </template>
            </el-table-column>

            <el-table-column label="事件信息" align="left" key="content" prop="content" v-if="columns[1].visible"
               :show-overflow-tooltip="true" />
            <el-table-column label="实体对象" align="center" width="130" key="keys" prop="keys" v-if="columns[6].visible"
               :show-overflow-tooltip="true" />
            <el-table-column label="类型" align="center" width="80" key="memoryType" prop="memoryType"
               v-if="columns[6].visible" :show-overflow-tooltip="true" />

            <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width"
               v-if="columns[8].visible">
               <template #default="scope">
                  <el-tooltip content="删除" placement="top" v-if="scope.row.applicationId !== 1">
                     <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                        v-hasPermi="['system:Memory:remove']"></el-button>
                  </el-tooltip>

                  <el-tooltip content="查看" placement="top" v-if="scope.row.applicationId !== 1">
                     <el-button @click="handleClickBalanced(scope.row)" type="text" bg circle title="请点击选中查看">
                        <el-icon>
                           <Right />
                        </el-icon>
                     </el-button>
                  </el-tooltip>

               </template>
            </el-table-column>
         </el-table>

         <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-drawer>

   </div>
</template>


<script setup>

import * as echarts from "echarts";
import {
   listMemory,
   delMemory,
   getMemory,
   updateMemory,
   addMemory,
} from "@/api/base/search/memory";
import { reactive } from "vue";

/// 声明定义一下echart
const echart = echarts;

const drawer = ref(false);

function drawCloudWord() {

   let myChart = echart.init(
      document.getElementById("echarts-cloudword-chart")
   );

   let graph = { "nodes": [{ "id": "0", "name": "Myriel", "symbolSize": 19.12381, "x": -266.82776, "y": 299.6904, "value": 28.685715, "category": 0 }, { "id": "1", "name": "Napoleon", "symbolSize": 2.66666666666667, "x": -418.08344, "y": 446.8853, "value": 4, "category": 0 }, { "id": "2", "name": "MlleBaptistine", "symbolSize": 6.32380933333333, "x": -212.76357, "y": 245.29176, "value": 9.485714, "category": 1 }, { "id": "3", "name": "MmeMagloire", "symbolSize": 6.32380933333333, "x": -242.82404, "y": 235.26283, "value": 9.485714, "category": 1 }, { "id": "4", "name": "CountessDeLo", "symbolSize": 2.66666666666667, "x": -379.30386, "y": 429.06424, "value": 4, "category": 0 }, { "id": "5", "name": "Geborand", "symbolSize": 2.66666666666667, "x": -417.26337, "y": 406.03506, "value": 4, "category": 0 }, { "id": "6", "name": "Champtercier", "symbolSize": 2.66666666666667, "x": -332.6012, "y": 485.16974, "value": 4, "category": 0 }, { "id": "7", "name": "Cravatte", "symbolSize": 2.66666666666667, "x": -382.69568, "y": 475.09113, "value": 4, "category": 0 }, { "id": "8", "name": "Count", "symbolSize": 2.66666666666667, "x": -320.384, "y": 387.17325, "value": 4, "category": 0 }, { "id": "9", "name": "OldMan", "symbolSize": 2.66666666666667, "x": -344.39832, "y": 451.16772, "value": 4, "category": 0 }, { "id": "10", "name": "Labarre", "symbolSize": 2.66666666666667, "x": -89.34107, "y": 234.56128, "value": 4, "category": 1 }, { "id": "11", "name": "Valjean", "symbolSize": 66.6666666666667, "x": -87.93029, "y": -6.8120565, "value": 100, "category": 1 }, { "id": "12", "name": "Marguerite", "symbolSize": 4.49523933333333, "x": -339.77908, "y": -184.69139, "value": 6.742859, "category": 1 }, { "id": "13", "name": "MmeDeR", "symbolSize": 2.66666666666667, "x": -194.31313, "y": 178.55301, "value": 4, "category": 1 }, { "id": "14", "name": "Isabeau", "symbolSize": 2.66666666666667, "x": -158.05168, "y": 201.99768, "value": 4, "category": 1 }, { "id": "15", "name": "Gervais", "symbolSize": 2.66666666666667, "x": -127.701546, "y": 242.55057, "value": 4, "category": 1 }, { "id": "16", "name": "Tholomyes", "symbolSize": 17.2952373333333, "x": -385.2226, "y": -393.5572, "value": 25.942856, "category": 2 }, { "id": "17", "name": "Listolier", "symbolSize": 13.6380973333333, "x": -516.55884, "y": -393.98975, "value": 20.457146, "category": 2 }, { "id": "18", "name": "Fameuil", "symbolSize": 13.6380973333333, "x": -464.79382, "y": -493.57944, "value": 20.457146, "category": 2 }, { "id": "19", "name": "Blacheville", "symbolSize": 13.6380973333333, "x": -515.1624, "y": -456.9891, "value": 20.457146, "category": 2 }, { "id": "20", "name": "Favourite", "symbolSize": 13.6380973333333, "x": -408.12122, "y": -464.5048, "value": 20.457146, "category": 2 }, { "id": "21", "name": "Dahlia", "symbolSize": 13.6380973333333, "x": -456.44113, "y": -425.13303, "value": 20.457146, "category": 2 }, { "id": "22", "name": "Zephine", "symbolSize": 13.6380973333333, "x": -459.1107, "y": -362.5133, "value": 20.457146, "category": 2 }, { "id": "23", "name": "Fantine", "symbolSize": 28.2666666666667, "x": -313.42786, "y": -289.44803, "value": 42.4, "category": 2 }, { "id": "24", "name": "MmeThenardier", "symbolSize": 20.9523826666667, "x": 4.6313396, "y": -273.8517, "value": 31.428574, "category": 7 }, { "id": "25", "name": "Thenardier", "symbolSize": 30.0952353333333, "x": 82.80825, "y": -203.1144, "value": 45.142853, "category": 7 }, { "id": "26", "name": "Cosette", "symbolSize": 20.9523826666667, "x": 78.64646, "y": -31.512747, "value": 31.428574, "category": 6 }, { "id": "27", "name": "Javert", "symbolSize": 31.9238066666667, "x": -81.46074, "y": -204.20204, "value": 47.88571, "category": 7 }, { "id": "28", "name": "Fauchelevent", "symbolSize": 8.152382, "x": -225.73984, "y": 82.41631, "value": 12.228573, "category": 4 }, { "id": "29", "name": "Bamatabois", "symbolSize": 15.4666666666667, "x": -385.6842, "y": -20.206686, "value": 23.2, "category": 3 }, { "id": "30", "name": "Perpetue", "symbolSize": 4.49523933333333, "x": -403.92447, "y": -197.69823, "value": 6.742859, "category": 2 }, { "id": "31", "name": "Simplice", "symbolSize": 8.152382, "x": -281.4253, "y": -158.45137, "value": 12.228573, "category": 2 }, { "id": "32", "name": "Scaufflaire", "symbolSize": 2.66666666666667, "x": -122.41348, "y": 210.37503, "value": 4, "category": 1 }, { "id": "33", "name": "Woman1", "symbolSize": 4.49523933333333, "x": -234.6001, "y": -113.15067, "value": 6.742859, "category": 1 }, { "id": "34", "name": "Judge", "symbolSize": 11.8095246666667, "x": -387.84915, "y": 58.7059, "value": 17.714287, "category": 3 }, { "id": "35", "name": "Champmathieu", "symbolSize": 11.8095246666667, "x": -338.2307, "y": 87.48405, "value": 17.714287, "category": 3 }, { "id": "36", "name": "Brevet", "symbolSize": 11.8095246666667, "x": -453.26874, "y": 58.94648, "value": 17.714287, "category": 3 }, { "id": "37", "name": "Chenildieu", "symbolSize": 11.8095246666667, "x": -386.44904, "y": 140.05937, "value": 17.714287, "category": 3 }, { "id": "38", "name": "Cochepaille", "symbolSize": 11.8095246666667, "x": -446.7876, "y": 123.38005, "value": 17.714287, "category": 3 }, { "id": "39", "name": "Pontmercy", "symbolSize": 6.32380933333333, "x": 336.49738, "y": -269.55914, "value": 9.485714, "category": 6 }, { "id": "40", "name": "Boulatruelle", "symbolSize": 2.66666666666667, "x": 29.187843, "y": -460.13132, "value": 4, "category": 7 }, { "id": "41", "name": "Eponine", "symbolSize": 20.9523826666667, "x": 238.36697, "y": -210.00926, "value": 31.428574, "category": 7 }, { "id": "42", "name": "Anzelma", "symbolSize": 6.32380933333333, "x": 189.69513, "y": -346.50662, "value": 9.485714, "category": 7 }, { "id": "43", "name": "Woman2", "symbolSize": 6.32380933333333, "x": -187.00418, "y": -145.02663, "value": 9.485714, "category": 6 }, { "id": "44", "name": "MotherInnocent", "symbolSize": 4.49523933333333, "x": -252.99521, "y": 129.87549, "value": 6.742859, "category": 4 }, { "id": "45", "name": "Gribier", "symbolSize": 2.66666666666667, "x": -296.07935, "y": 163.11964, "value": 4, "category": 4 }, { "id": "46", "name": "Jondrette", "symbolSize": 2.66666666666667, "x": 550.3201, "y": 522.4031, "value": 4, "category": 5 }, { "id": "47", "name": "MmeBurgon", "symbolSize": 4.49523933333333, "x": 488.13535, "y": 356.8573, "value": 6.742859, "category": 5 }, { "id": "48", "name": "Gavroche", "symbolSize": 41.0666706666667, "x": 387.89572, "y": 110.462326, "value": 61.600006, "category": 8 }, { "id": "49", "name": "Gillenormand", "symbolSize": 13.6380973333333, "x": 126.4831, "y": 68.10622, "value": 20.457146, "category": 6 }, { "id": "50", "name": "Magnon", "symbolSize": 4.49523933333333, "x": 127.07365, "y": -113.05923, "value": 6.742859, "category": 6 }, { "id": "51", "name": "MlleGillenormand", "symbolSize": 13.6380973333333, "x": 162.63559, "y": 117.6565, "value": 20.457146, "category": 6 }, { "id": "52", "name": "MmePontmercy", "symbolSize": 4.49523933333333, "x": 353.66415, "y": -205.89165, "value": 6.742859, "category": 6 }, { "id": "53", "name": "MlleVaubois", "symbolSize": 2.66666666666667, "x": 165.43939, "y": 339.7736, "value": 4, "category": 6 }, { "id": "54", "name": "LtGillenormand", "symbolSize": 8.152382, "x": 137.69348, "y": 196.1069, "value": 12.228573, "category": 6 }, { "id": "55", "name": "Marius", "symbolSize": 35.5809533333333, "x": 206.44687, "y": -13.805411, "value": 53.37143, "category": 6 }, { "id": "56", "name": "BaronessT", "symbolSize": 4.49523933333333, "x": 194.82993, "y": 224.78036, "value": 6.742859, "category": 6 }, { "id": "57", "name": "Mabeuf", "symbolSize": 20.9523826666667, "x": 597.6618, "y": 135.18481, "value": 31.428574, "category": 8 }, { "id": "58", "name": "Enjolras", "symbolSize": 28.2666666666667, "x": 355.78366, "y": -74.882454, "value": 42.4, "category": 8 }, { "id": "59", "name": "Combeferre", "symbolSize": 20.9523826666667, "x": 515.2961, "y": -46.167564, "value": 31.428574, "category": 8 }, { "id": "60", "name": "Prouvaire", "symbolSize": 17.2952373333333, "x": 614.29285, "y": -69.3104, "value": 25.942856, "category": 8 }, { "id": "61", "name": "Feuilly", "symbolSize": 20.9523826666667, "x": 550.1917, "y": -128.17537, "value": 31.428574, "category": 8 }, { "id": "62", "name": "Courfeyrac", "symbolSize": 24.6095266666667, "x": 436.17184, "y": -12.7286825, "value": 36.91429, "category": 8 }, { "id": "63", "name": "Bahorel", "symbolSize": 22.7809533333333, "x": 602.55225, "y": 16.421427, "value": 34.17143, "category": 8 }, { "id": "64", "name": "Bossuet", "symbolSize": 24.6095266666667, "x": 455.81955, "y": -115.45826, "value": 36.91429, "category": 8 }, { "id": "65", "name": "Joly", "symbolSize": 22.7809533333333, "x": 516.40784, "y": 47.242233, "value": 34.17143, "category": 8 }, { "id": "66", "name": "Grantaire", "symbolSize": 19.12381, "x": 646.4313, "y": -151.06331, "value": 28.685715, "category": 8 }, { "id": "67", "name": "MotherPlutarch", "symbolSize": 2.66666666666667, "x": 668.9568, "y": 204.65488, "value": 4, "category": 8 }, { "id": "68", "name": "Gueulemer", "symbolSize": 19.12381, "x": 78.4799, "y": -347.15146, "value": 28.685715, "category": 7 }, { "id": "69", "name": "Babet", "symbolSize": 19.12381, "x": 150.35959, "y": -298.50797, "value": 28.685715, "category": 7 }, { "id": "70", "name": "Claquesous", "symbolSize": 19.12381, "x": 137.3717, "y": -410.2809, "value": 28.685715, "category": 7 }, { "id": "71", "name": "Montparnasse", "symbolSize": 17.2952373333333, "x": 234.87747, "y": -400.85983, "value": 25.942856, "category": 7 }, { "id": "72", "name": "Toussaint", "symbolSize": 6.32380933333333, "x": 40.942253, "y": 113.78272, "value": 9.485714, "category": 1 }, { "id": "73", "name": "Child1", "symbolSize": 4.49523933333333, "x": 437.939, "y": 291.58234, "value": 6.742859, "category": 8 }, { "id": "74", "name": "Child2", "symbolSize": 4.49523933333333, "x": 466.04922, "y": 283.3606, "value": 6.742859, "category": 8 }, { "id": "75", "name": "Brujon", "symbolSize": 13.6380973333333, "x": 238.79364, "y": -314.06345, "value": 20.457146, "category": 7 }, { "id": "76", "name": "MmeHucheloup", "symbolSize": 13.6380973333333, "x": 712.18353, "y": 4.8131495, "value": 20.457146, "category": 8 }], "links": [{ "source": "1", "target": "0" }, { "source": "2", "target": "0" }, { "source": "3", "target": "0" }, { "source": "3", "target": "2" }, { "source": "4", "target": "0" }, { "source": "5", "target": "0" }, { "source": "6", "target": "0" }, { "source": "7", "target": "0" }, { "source": "8", "target": "0" }, { "source": "9", "target": "0" }, { "source": "11", "target": "0" }, { "source": "11", "target": "2" }, { "source": "11", "target": "3" }, { "source": "11", "target": "10" }, { "source": "12", "target": "11" }, { "source": "13", "target": "11" }, { "source": "14", "target": "11" }, { "source": "15", "target": "11" }, { "source": "17", "target": "16" }, { "source": "18", "target": "16" }, { "source": "18", "target": "17" }, { "source": "19", "target": "16" }, { "source": "19", "target": "17" }, { "source": "19", "target": "18" }, { "source": "20", "target": "16" }, { "source": "20", "target": "17" }, { "source": "20", "target": "18" }, { "source": "20", "target": "19" }, { "source": "21", "target": "16" }, { "source": "21", "target": "17" }, { "source": "21", "target": "18" }, { "source": "21", "target": "19" }, { "source": "21", "target": "20" }, { "source": "22", "target": "16" }, { "source": "22", "target": "17" }, { "source": "22", "target": "18" }, { "source": "22", "target": "19" }, { "source": "22", "target": "20" }, { "source": "22", "target": "21" }, { "source": "23", "target": "11" }, { "source": "23", "target": "12" }, { "source": "23", "target": "16" }, { "source": "23", "target": "17" }, { "source": "23", "target": "18" }, { "source": "23", "target": "19" }, { "source": "23", "target": "20" }, { "source": "23", "target": "21" }, { "source": "23", "target": "22" }, { "source": "24", "target": "11" }, { "source": "24", "target": "23" }, { "source": "25", "target": "11" }, { "source": "25", "target": "23" }, { "source": "25", "target": "24" }, { "source": "26", "target": "11" }, { "source": "26", "target": "16" }, { "source": "26", "target": "24" }, { "source": "26", "target": "25" }, { "source": "27", "target": "11" }, { "source": "27", "target": "23" }, { "source": "27", "target": "24" }, { "source": "27", "target": "25" }, { "source": "27", "target": "26" }, { "source": "28", "target": "11" }, { "source": "28", "target": "27" }, { "source": "29", "target": "11" }, { "source": "29", "target": "23" }, { "source": "29", "target": "27" }, { "source": "30", "target": "23" }, { "source": "31", "target": "11" }, { "source": "31", "target": "23" }, { "source": "31", "target": "27" }, { "source": "31", "target": "30" }, { "source": "32", "target": "11" }, { "source": "33", "target": "11" }, { "source": "33", "target": "27" }, { "source": "34", "target": "11" }, { "source": "34", "target": "29" }, { "source": "35", "target": "11" }, { "source": "35", "target": "29" }, { "source": "35", "target": "34" }, { "source": "36", "target": "11" }, { "source": "36", "target": "29" }, { "source": "36", "target": "34" }, { "source": "36", "target": "35" }, { "source": "37", "target": "11" }, { "source": "37", "target": "29" }, { "source": "37", "target": "34" }, { "source": "37", "target": "35" }, { "source": "37", "target": "36" }, { "source": "38", "target": "11" }, { "source": "38", "target": "29" }, { "source": "38", "target": "34" }, { "source": "38", "target": "35" }, { "source": "38", "target": "36" }, { "source": "38", "target": "37" }, { "source": "39", "target": "25" }, { "source": "40", "target": "25" }, { "source": "41", "target": "24" }, { "source": "41", "target": "25" }, { "source": "42", "target": "24" }, { "source": "42", "target": "25" }, { "source": "42", "target": "41" }, { "source": "43", "target": "11" }, { "source": "43", "target": "26" }, { "source": "43", "target": "27" }, { "source": "44", "target": "11" }, { "source": "44", "target": "28" }, { "source": "45", "target": "28" }, { "source": "47", "target": "46" }, { "source": "48", "target": "11" }, { "source": "48", "target": "25" }, { "source": "48", "target": "27" }, { "source": "48", "target": "47" }, { "source": "49", "target": "11" }, { "source": "49", "target": "26" }, { "source": "50", "target": "24" }, { "source": "50", "target": "49" }, { "source": "51", "target": "11" }, { "source": "51", "target": "26" }, { "source": "51", "target": "49" }, { "source": "52", "target": "39" }, { "source": "52", "target": "51" }, { "source": "53", "target": "51" }, { "source": "54", "target": "26" }, { "source": "54", "target": "49" }, { "source": "54", "target": "51" }, { "source": "55", "target": "11" }, { "source": "55", "target": "16" }, { "source": "55", "target": "25" }, { "source": "55", "target": "26" }, { "source": "55", "target": "39" }, { "source": "55", "target": "41" }, { "source": "55", "target": "48" }, { "source": "55", "target": "49" }, { "source": "55", "target": "51" }, { "source": "55", "target": "54" }, { "source": "56", "target": "49" }, { "source": "56", "target": "55" }, { "source": "57", "target": "41" }, { "source": "57", "target": "48" }, { "source": "57", "target": "55" }, { "source": "58", "target": "11" }, { "source": "58", "target": "27" }, { "source": "58", "target": "48" }, { "source": "58", "target": "55" }, { "source": "58", "target": "57" }, { "source": "59", "target": "48" }, { "source": "59", "target": "55" }, { "source": "59", "target": "57" }, { "source": "59", "target": "58" }, { "source": "60", "target": "48" }, { "source": "60", "target": "58" }, { "source": "60", "target": "59" }, { "source": "61", "target": "48" }, { "source": "61", "target": "55" }, { "source": "61", "target": "57" }, { "source": "61", "target": "58" }, { "source": "61", "target": "59" }, { "source": "61", "target": "60" }, { "source": "62", "target": "41" }, { "source": "62", "target": "48" }, { "source": "62", "target": "55" }, { "source": "62", "target": "57" }, { "source": "62", "target": "58" }, { "source": "62", "target": "59" }, { "source": "62", "target": "60" }, { "source": "62", "target": "61" }, { "source": "63", "target": "48" }, { "source": "63", "target": "55" }, { "source": "63", "target": "57" }, { "source": "63", "target": "58" }, { "source": "63", "target": "59" }, { "source": "63", "target": "60" }, { "source": "63", "target": "61" }, { "source": "63", "target": "62" }, { "source": "64", "target": "11" }, { "source": "64", "target": "48" }, { "source": "64", "target": "55" }, { "source": "64", "target": "57" }, { "source": "64", "target": "58" }, { "source": "64", "target": "59" }, { "source": "64", "target": "60" }, { "source": "64", "target": "61" }, { "source": "64", "target": "62" }, { "source": "64", "target": "63" }, { "source": "65", "target": "48" }, { "source": "65", "target": "55" }, { "source": "65", "target": "57" }, { "source": "65", "target": "58" }, { "source": "65", "target": "59" }, { "source": "65", "target": "60" }, { "source": "65", "target": "61" }, { "source": "65", "target": "62" }, { "source": "65", "target": "63" }, { "source": "65", "target": "64" }, { "source": "66", "target": "48" }, { "source": "66", "target": "58" }, { "source": "66", "target": "59" }, { "source": "66", "target": "60" }, { "source": "66", "target": "61" }, { "source": "66", "target": "62" }, { "source": "66", "target": "63" }, { "source": "66", "target": "64" }, { "source": "66", "target": "65" }, { "source": "67", "target": "57" }, { "source": "68", "target": "11" }, { "source": "68", "target": "24" }, { "source": "68", "target": "25" }, { "source": "68", "target": "27" }, { "source": "68", "target": "41" }, { "source": "68", "target": "48" }, { "source": "69", "target": "11" }, { "source": "69", "target": "24" }, { "source": "69", "target": "25" }, { "source": "69", "target": "27" }, { "source": "69", "target": "41" }, { "source": "69", "target": "48" }, { "source": "69", "target": "68" }, { "source": "70", "target": "11" }, { "source": "70", "target": "24" }, { "source": "70", "target": "25" }, { "source": "70", "target": "27" }, { "source": "70", "target": "41" }, { "source": "70", "target": "58" }, { "source": "70", "target": "68" }, { "source": "70", "target": "69" }, { "source": "71", "target": "11" }, { "source": "71", "target": "25" }, { "source": "71", "target": "27" }, { "source": "71", "target": "41" }, { "source": "71", "target": "48" }, { "source": "71", "target": "68" }, { "source": "71", "target": "69" }, { "source": "71", "target": "70" }, { "source": "72", "target": "11" }, { "source": "72", "target": "26" }, { "source": "72", "target": "27" }, { "source": "73", "target": "48" }, { "source": "74", "target": "48" }, { "source": "74", "target": "73" }, { "source": "75", "target": "25" }, { "source": "75", "target": "41" }, { "source": "75", "target": "48" }, { "source": "75", "target": "68" }, { "source": "75", "target": "69" }, { "source": "75", "target": "70" }, { "source": "75", "target": "71" }, { "source": "76", "target": "48" }, { "source": "76", "target": "58" }, { "source": "76", "target": "62" }, { "source": "76", "target": "63" }, { "source": "76", "target": "64" }, { "source": "76", "target": "65" }, { "source": "76", "target": "66" }], "categories": [{ "name": "A" }, { "name": "B" }, { "name": "C" }, { "name": "D" }, { "name": "E" }, { "name": "F" }, { "name": "G" }, { "name": "H" }, { "name": "I" }] };

   graph.nodes.forEach(function (node) {
      node.label = {
         show: node.symbolSize > 30
      };
   });
   let option = {
      title: {
         text: 'Les Miserables',
         subtext: 'Default layout',
         top: 'bottom',
         left: 'right'
      },
      tooltip: {},
      legend: [
         {
            // selectedMode: 'single',
            data: graph.categories.map(function (a) {
               return a.name;
            })
         }
      ],
      animationDuration: 1500,
      animationEasingUpdate: 'quinticInOut',
      series: [
         {
            name: 'Les Miserables',
            type: 'graph',
            legendHoverLink: false,
            layout: 'none',
            data: graph.nodes,
            links: graph.links,
            categories: graph.categories,
            roam: true,
            label: {
               position: 'right',
               formatter: '{b}'
            },
            lineStyle: {
               color: 'source',
               curveness: 0.3
            },
            emphasis: {
               focus: 'adjacency',
               lineStyle: {
                  width: 10
               }
            }
         }
      ]
   };
   myChart.setOption(option);

}

function drawLine() {
   let myChart = echart.init(
      document.getElementById("echarts-entityMapper-chart")
   );

   let data = {
      "nodes": [
         { "name": "Agricultural 'waste'" },
         { "name": "Bio-conversion" },
         { "name": "Liquid" },
         { "name": "Losses" },
         { "name": "Solid" },
         { "name": "Gas" },
         { "name": "Biofuel imports" },
         { "name": "Biomass imports" },
         { "name": "Coal imports" },
         { "name": "Coal" },
         { "name": "Coal reserves" },
         { "name": "District heating" },
         { "name": "Industry" },
         { "name": "Heating and cooling - commercial" },
         { "name": "Heating and cooling - homes" },
         { "name": "Electricity grid" },
         { "name": "Over generation / exports" },
         { "name": "H2 conversion" },
         { "name": "Road transport" },
         { "name": "Agriculture" },
         { "name": "Rail transport" },
         { "name": "Lighting & appliances - commercial" },
         { "name": "Lighting & appliances - homes" },
         { "name": "Gas imports" },
         { "name": "Ngas" },
         { "name": "Gas reserves" },
         { "name": "Thermal generation" },
         { "name": "Geothermal" },
         { "name": "H2" },
         { "name": "Hydro" },
         { "name": "International shipping" },
         { "name": "Domestic aviation" },
         { "name": "International aviation" },
         { "name": "National navigation" },
         { "name": "Marine algae" },
         { "name": "Nuclear" },
         { "name": "Oil imports" },
         { "name": "Oil" },
         { "name": "Oil reserves" },
         { "name": "Other waste" },
         { "name": "Pumped heat" },
         { "name": "Solar PV" },
         { "name": "Solar Thermal" },
         { "name": "Solar" },
         { "name": "Tidal" },
         { "name": "UK land based bioenergy" },
         { "name": "Wave" },
         { "name": "Wind" }
      ],
      "links": [
         { "source": "Agricultural 'waste'", "target": "Bio-conversion", "value": 124.729 },
         { "source": "Bio-conversion", "target": "Liquid", "value": 0.597 },
         { "source": "Bio-conversion", "target": "Losses", "value": 26.862 },
         { "source": "Bio-conversion", "target": "Solid", "value": 280.322 },
         { "source": "Bio-conversion", "target": "Gas", "value": 81.144 },
         { "source": "Biofuel imports", "target": "Liquid", "value": 35 },
         { "source": "Biomass imports", "target": "Solid", "value": 35 },
         { "source": "Coal imports", "target": "Coal", "value": 11.606 },
         { "source": "Coal reserves", "target": "Coal", "value": 63.965 },
         { "source": "Coal", "target": "Solid", "value": 75.571 },
         { "source": "District heating", "target": "Industry", "value": 10.639 },
         { "source": "District heating", "target": "Heating and cooling - commercial", "value": 22.505 },
         { "source": "District heating", "target": "Heating and cooling - homes", "value": 46.184 },
         { "source": "Electricity grid", "target": "Over generation / exports", "value": 104.453 },
         { "source": "Electricity grid", "target": "Heating and cooling - homes", "value": 113.726 },
         { "source": "Electricity grid", "target": "H2 conversion", "value": 27.14 },
         { "source": "Electricity grid", "target": "Industry", "value": 342.165 },
         { "source": "Electricity grid", "target": "Road transport", "value": 37.797 },
         { "source": "Electricity grid", "target": "Agriculture", "value": 4.412 },
         { "source": "Electricity grid", "target": "Heating and cooling - commercial", "value": 40.858 },
         { "source": "Electricity grid", "target": "Losses", "value": 56.691 },
         { "source": "Electricity grid", "target": "Rail transport", "value": 7.863 },
         { "source": "Electricity grid", "target": "Lighting & appliances - commercial", "value": 90.008 },
         { "source": "Electricity grid", "target": "Lighting & appliances - homes", "value": 93.494 },
         { "source": "Gas imports", "target": "Ngas", "value": 40.719 },
         { "source": "Gas reserves", "target": "Ngas", "value": 82.233 },
         { "source": "Gas", "target": "Heating and cooling - commercial", "value": 0.129 },
         { "source": "Gas", "target": "Losses", "value": 1.401 },
         { "source": "Gas", "target": "Thermal generation", "value": 151.891 },
         { "source": "Gas", "target": "Agriculture", "value": 2.096 },
         { "source": "Gas", "target": "Industry", "value": 48.58 },
         { "source": "Geothermal", "target": "Electricity grid", "value": 7.013 },
         { "source": "H2 conversion", "target": "H2", "value": 20.897 },
         { "source": "H2 conversion", "target": "Losses", "value": 6.242 },
         { "source": "H2", "target": "Road transport", "value": 20.897 },
         { "source": "Hydro", "target": "Electricity grid", "value": 6.995 },
         { "source": "Liquid", "target": "Industry", "value": 121.066 },
         { "source": "Liquid", "target": "International shipping", "value": 128.69 },
         { "source": "Liquid", "target": "Road transport", "value": 135.835 },
         { "source": "Liquid", "target": "Domestic aviation", "value": 14.458 },
         { "source": "Liquid", "target": "International aviation", "value": 206.267 },
         { "source": "Liquid", "target": "Agriculture", "value": 3.64 },
         { "source": "Liquid", "target": "National navigation", "value": 33.218 },
         { "source": "Liquid", "target": "Rail transport", "value": 4.413 },
         { "source": "Marine algae", "target": "Bio-conversion", "value": 4.375 },
         { "source": "Ngas", "target": "Gas", "value": 122.952 },
         { "source": "Nuclear", "target": "Thermal generation", "value": 839.978 },
         { "source": "Oil imports", "target": "Oil", "value": 504.287 },
         { "source": "Oil reserves", "target": "Oil", "value": 107.703 },
         { "source": "Oil", "target": "Liquid", "value": 611.99 },
         { "source": "Other waste", "target": "Solid", "value": 56.587 },
         { "source": "Other waste", "target": "Bio-conversion", "value": 77.81 },
         { "source": "Pumped heat", "target": "Heating and cooling - homes", "value": 193.026 },
         { "source": "Pumped heat", "target": "Heating and cooling - commercial", "value": 70.672 },
         { "source": "Solar PV", "target": "Electricity grid", "value": 59.901 },
         { "source": "Solar Thermal", "target": "Heating and cooling - homes", "value": 19.263 },
         { "source": "Solar", "target": "Solar Thermal", "value": 19.263 },
         { "source": "Solar", "target": "Solar PV", "value": 59.901 },
         { "source": "Solid", "target": "Agriculture", "value": 0.882 },
         { "source": "Solid", "target": "Thermal generation", "value": 400.12 },
         { "source": "Solid", "target": "Industry", "value": 46.477 },
         { "source": "Thermal generation", "target": "Electricity grid", "value": 525.531 },
         { "source": "Thermal generation", "target": "Losses", "value": 787.129 },
         { "source": "Thermal generation", "target": "District heating", "value": 79.329 },
         { "source": "Tidal", "target": "Electricity grid", "value": 9.452 },
         { "source": "UK land based bioenergy", "target": "Bio-conversion", "value": 182.01 },
         { "source": "Wave", "target": "Electricity grid", "value": 19.013 },
         { "source": "Wind", "target": "Electricity grid", "value": 289.366 }
      ]
   };

   myChart.hideLoading();
   myChart.setOption(
      ({
         tooltip: {
            trigger: 'item',
            triggerOn: 'mousemove'
         },
         animation: false,
         series: [
            {
               type: 'sankey',
               emphasis: {
                  focus: 'adjacency'
               },
               nodeAlign: 'right',
               data: data.nodes,
               links: data.links,
               lineStyle: {
                  color: 'source',
                  curveness: 0.5
               }
            }
         ]
      })
   );
}

function drawBar2() {
   let barChart = echart.init(
      document.getElementById("echarts-bar2-chart")
   );

   var lineOption;

   // prettier-ignore
   const hours = [
      '12a', '1a', '2a', '3a', '4a', '5a', '6a',
      '7a', '8a', '9a', '10a', '11a',
      '12p', '1p', '2p', '3p', '4p', '5p',
      '6p', '7p', '8p', '9p', '10p', '11p'
   ];
   // prettier-ignore
   const days = ['星期六', '星期五', '星期四', '星期三', '星期二', '星期一', '星期日'];

   // prettier-ignore
   const data = [[0, 0, 5], [0, 1, 1], [0, 2, 0], [0, 3, 0], [0, 4, 0], [0, 5, 0],
   [1, 7, 0], [1, 8, 0], [1, 9, 0], [1, 10, 5], [1, 11, 2], [1, 12, 2],
   [1, 13, 6], [1, 14, 9], [1, 15, 11], [1, 16, 6], [1, 17, 7], [1, 18, 8],
   [1, 19, 12], [1, 20, 5], [1, 21, 5], [1, 22, 7], [1, 23, 2], [2, 0, 1],
   [4, 2, 0], [4, 3, 0], [4, 4, 0], [4, 5, 1], [4, 6, 0], [4, 7, 0], [4, 8, 0],
   [4, 9, 2], [4, 10, 4], [4, 11, 4], [4, 12, 2], [4, 13, 4], [4, 14, 4], [4, 15, 14],
   [4, 16, 12], [4, 17, 1], [4, 18, 8], [4, 19, 5], [4, 20, 3], [4, 21, 7], [4, 22, 3], [4, 23, 0], [5, 0, 2], [5, 1, 1], [5, 2, 0],
   [0, 6, 0], [0, 7, 0], [0, 8, 0], [0, 9, 0], [0, 10, 0], [0, 11, 2],
   [0, 12, 4], [0, 13, 1], [0, 14, 1], [0, 15, 3], [0, 16, 4], [0, 17, 6],
   [0, 18, 4], [0, 19, 4], [0, 20, 3], [0, 21, 3], [0, 22, 2], [0, 23, 5],
   [2, 8, 0], [2, 9, 0], [2, 10, 3], [2, 11, 2], [2, 12, 1], [2, 13, 9], [2, 14, 8],
   [2, 15, 10], [2, 16, 6], [2, 17, 5], [2, 18, 5], [2, 19, 5], [2, 20, 7], [2, 21, 4],
   [1, 0, 7], [1, 1, 0], [1, 2, 0], [1, 3, 0], [1, 4, 0], [1, 5, 0], [1, 6, 0],
   [2, 1, 1], [2, 2, 0], [2, 3, 0], [2, 4, 0], [2, 5, 0], [2, 6, 0], [2, 7, 0],
   [2, 22, 2], [2, 23, 4], [3, 0, 7], [3, 1, 3], [3, 2, 0], [3, 3, 0], [3, 4, 0],
   [3, 5, 0], [3, 6, 0], [3, 7, 0], [3, 8, 1], [3, 9, 0], [3, 10, 5], [3, 11, 4],
   [3, 12, 7], [3, 13, 14], [3, 14, 13], [3, 15, 12], [3, 16, 9], [3, 17, 5], [3, 18, 5],
   [3, 19, 10], [3, 20, 6], [3, 21, 4], [3, 22, 4], [3, 23, 1], [4, 0, 1], [4, 1, 3],
   [5, 3, 3], [5, 4, 0], [5, 5, 0], [5, 6, 0], [5, 7, 0], [5, 8, 2], [5, 9, 0], [5, 10, 4], [5, 11, 1], [5, 12, 5], [5, 13, 10], [5, 14, 5], [5, 15, 7], [5, 16, 11], [5, 17, 6], [5, 18, 0], [5, 19, 5], [5, 20, 3], [5, 21, 4], [5, 22, 2], [5, 23, 0], [6, 0, 1], [6, 1, 0], [6, 2, 0], [6, 3, 0], [6, 4, 0], [6, 5, 0], [6, 6, 0], [6, 7, 0], [6, 8, 0], [6, 9, 0], [6, 10, 1], [6, 11, 0], [6, 12, 2], [6, 13, 1], [6, 14, 3], [6, 15, 4], [6, 16, 0], [6, 17, 0], [6, 18, 0], [6, 19, 0], [6, 20, 1], [6, 21, 2], [6, 22, 2], [6, 23, 6]]
      .map(function (item) {
         return [item[1], item[0], item[2] || '-'];
      });

   lineOption = {
      tooltip: {
         position: 'top'
      },
      grid: {
         height: '90%',
         width: '90%',
         top: '1%'
      },
      xAxis: {
         type: 'category',
         data: hours,
         splitArea: {
            show: true
         }
      },
      yAxis: {
         type: 'category',
         data: days,
         splitArea: {
            show: true
         }
      },
      visualMap: {
         min: 0,
         max: 10,
         calculable: true,
         orient: '',
         left: 'left',
         bottom: '0%'
      },
      series: [
         {
            name: '异常次数',
            type: 'heatmap',
            data: data,
            label: {
               show: true
            },
            emphasis: {
               itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
               }
            }
         }
      ]
   };

   barChart.setOption(lineOption);

   barChart.off('click');
   barChart.on('click', function (e) {
      console.log(e)
      drawer.value = true;
   });

};

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_Memory_sex } = proxy.useDict("sys_normal_disable", "sys_Memory_sex");

const MemoryList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const deptName = ref("");
const deptOptions = ref(undefined);
const initPassword = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);

/*** 应用导入参数 */
const upload = reactive({
   // 是否显示弹出层（应用导入）
   open: false,
   // 弹出层标题（应用导入）
   title: "",
   // 是否禁用上传
   isUploading: false,
   // 是否更新已经存在的应用数据
   updateSupport: 0,
   // 设置上传的请求头部
   headers: { Authorization: "Bearer " },
   // 上传的地址
   url: import.meta.env.VITE_APP_BASE_API + "/system/Memory/importData"
});

// 列显隐信息
const columns = ref([
   { key: 0, label: `图标`, visible: true },
   { key: 1, label: `记忆库名称`, visible: true },
   { key: 2, label: `所有者`, visible: true },
   { key: 3, label: `描述信息`, visible: true },
   { key: 4, label: `状态`, visible: true },
   { key: 5, label: `访问权限`, visible: true },
   { key: 6, label: `数据总量`, visible: true },
   { key: 7, label: `创建时间`, visible: true },
   { key: 8, label: `编辑`, visible: true },

]);

const data = reactive({
   form: {},
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      isMemory: 1,
      MemoryName: undefined,
      name: undefined,
      ownerId: undefined,
      status: undefined,
      deptId: undefined
   },
   rules: {
      applicationId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
      name: [{ required: true, message: "应用名称不能为空", trigger: "blur" }, {
         min: 2,
         max: 20,
         message: "应用名称长度必须介于 2 和 20 之间",
         trigger: "blur"
      }],
      ownerId: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
      description: [{ required: true, message: "描述信息不能为空", trigger: "blur" }],
      datasetStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
      accessPermission: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
      datasetSize: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
   }
});

const { queryParams, form, rules } = toRefs(data);


/** 查询应用列表 */
function getList() {
   loading.value = true;
   listMemory(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      MemoryList.value = res.rows;
      total.value = res.total;
   });
};

/** 返回 */
function goBack() {
  router.back();
}

/** 搜索按钮操作 */
function handleQuery() {
   console.log(queryParams);
   queryParams.value.pageNum = 1;
   getList();
};

/** 重置按钮操作 */
function resetQuery() {
   dateRange.value = [];
   proxy.resetForm("queryRef");
   queryParams.value.name = undefined;
   queryParams.value.ownerId = undefined;
   proxy.$refs.deptTreeRef.setCurrentKey(null);
   handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
   const applicationIds = row.id || ids.value;

   proxy.$modal.confirm('是否确认删除应用编号为"' + applicationIds + '"的数据项？').then(function () {
      return delMemory(applicationIds);
   }).then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
   }).catch(() => {
   });
};

/** 选择条数  */
function handleSelectionChange(selection) {
   ids.value = selection.map(item => item.id);
   single.value = selection.length != 1;
   multiple.value = !selection.length;
};

/** 重置操作表单 */
function reset() {
   form.value = {
      applicationId: undefined,
      name: undefined,
      ownerId: undefined,
      description: undefined,
      datasetStatus: undefined,
      accessPermission: undefined,
      datasetSize: undefined,
   };
   proxy.resetForm("MemoryRef");
};

/** 取消按钮 */
function cancel() {
   open.value = false;
   reset();
};

/** 新增按钮操作 */
function handleAdd() {
   reset();
   open.value = true;
   title.value = "添加应用";
};

/** 修改按钮操作 */
function handleUpdate(row) {
   reset();
   const applicationId = row.id || ids.value;
   getMemory(applicationId).then(response => {
      form.value = response.data;
      form.value.applicationId = applicationId;
      open.value = true;
      title.value = "修改应用";

   });
};

/** 导入按钮操作 */
function handleImport() {
   upload.title = "记忆库导入";
   upload.open = true;
};

/** 下载模板操作 */
function importTemplate() {
   proxy.download("system/user/importTemplate", {
   }, `user_template_${new Date().getTime()}.xlsx`);
};

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
   upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
   upload.open = false;
   upload.isUploading = false;
   proxy.$refs["uploadRef"].handleRemove(file);
   proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
   getList();
};
/** 提交上传文件 */
function submitFileForm() {
   proxy.$refs["uploadRef"].submit();
};

/** 提交按钮 */
function submitForm() {
   proxy.$refs["MemoryRef"].validate(valid => {
      if (valid) {
         form.value.isMemory = 1

         if (form.value.applicationId != undefined) {
            updateMemory(form.value).then(response => {
               proxy.$modal.msgSuccess("修改成功");
               open.value = false;
               getList();
            });
         } else {
            addMemory(form.value).then(response => {
               proxy.$modal.msgSuccess("新增成功");
               open.value = false;
               getList();
            });
         }
      }
   });
};


onMounted(() => {
   drawBar2();
   drawCloudWord();
   drawLine();
   getList();
});

onUnmounted(() => {
   echart.dispose;
});


</script>

<style lang="scss" scoped>
.memory-display-container {
   .panel-header {
      padding: 10px 0px;

      .header-desc {
         font-size: 13px;
         padding: 10px;
      }
   }

   .body-desc {
      font-size: 13px;
      padding: 10px;
   }
}
</style>
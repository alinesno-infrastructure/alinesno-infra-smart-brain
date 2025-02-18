<template>
  <div style="padding-bottom: 30px;">
        <div style="width:700px;margin:auto">
            <el-form :model="form" label-position="top" label-width="120px">
                <el-form-item label="频道图标" prop="icon">
                    <el-col :span="11">
                        <el-avatar  src="http://data.linesno.com/icons/sepcialist/dataset_55.png" />
                    </el-col>
                </el-form-item>
                <el-form-item label="数据集名称">
                    <el-col :span="11">
                        <el-input v-model="form.name" />
                    </el-col>
                </el-form-item>
                <el-form-item label="文件处理模型">
                    <el-select   v-model="form.region" placeholder="please select your zone">
                        <el-option label="Zone one" value="shanghai" />
                        <el-option label="Zone two" value="beijing" />
                    </el-select>
                </el-form-item>
                <el-form-item label="单条数据上限">
                    <el-col :span="11">
                        <el-input  v-model="form.name" />
                    </el-col>
                </el-form-item>
                <el-form-item label="使用权限">
                    <el-radio-group   v-model="form.resource">
                        <el-radio label="私有" />
                        <el-radio label="团队" />
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="文档描述">
                    <el-input  v-model="form.desc" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit">保存</el-button>
                    <el-button>删除</el-button>
                </el-form-item>
            </el-form>
        </div>

    </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'

// do not use same name with ref
const form = reactive({
    name: '',
    region: '',
    date1: '',
    date2: '',
    delivery: false,
    type: [],
    resource: '',
    desc: '',
})

const onSubmit = () => {
    console.log('submit!')
}
</script>

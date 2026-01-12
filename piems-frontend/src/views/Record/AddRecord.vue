<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="500px"
    @close="resetForm"
  >
    <el-form :model="recordForm" label-width="100px" :rules="rules" ref="recordFormRef">
      <el-form-item label="收支类型" prop="type">
        <el-select v-model="recordForm.type" placeholder="请选择收支类型">
          <el-option label="收入" value="1"></el-option>
          <el-option label="支出" value="2"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="金额" prop="amount">
        <el-input-number
          v-model="recordForm.amount"
          :min="0.01"
          :step="0.01"
          :precision="2"
          placeholder="请输入金额"
          style="width: 100%"
        ></el-input-number>
      </el-form-item>
      
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="recordForm.categoryId" placeholder="请选择分类">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="日期" prop="recordTime">
        <el-date-picker
          v-model="recordForm.recordTime"
          type="datetime"
          placeholder="选择日期时间"
          style="width: 100%"
        ></el-date-picker>
      </el-form-item>
      
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="recordForm.remark"
          type="textarea"
          placeholder="请输入备注"
          :rows="3"
        ></el-input>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">{{ submitButtonText }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AddRecord',
  emits: ['success', 'close'],
  
  data() {
    return {
      dialogVisible: false,
      isEditMode: false,
      recordForm: {
        id: '',
        type: '',
        amount: '',
        categoryId: '',
        recordTime: new Date(),
        remark: ''
      },
      categories: [],
      rules: {
        type: [{ required: true, message: '请选择收支类型', trigger: 'change' }],
        amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        recordTime: [{ required: true, message: '请选择日期', trigger: 'change' }]
      }
    }
  },
  
  computed: {
    dialogTitle() {
      return this.isEditMode ? '编辑收支记录' : '添加收支记录'
    },
    submitButtonText() {
      return this.isEditMode ? '保存' : '确定'
    }
  },
  
  mounted() {
    this.getCategories()
  },
  
  methods: {
    // 获取分类列表
    async getCategories() {
      try {
        const userId = JSON.parse(localStorage.getItem('user')).id
        // 获取所有分类（收入和支出）
        const incomeRes = await request.get('/category/list', {
          params: { type: 1, userId }
        })
        
        const expenseRes = await request.get('/category/list', {
          params: { type: 2, userId }
        })
        
        if (incomeRes.code === 200 && expenseRes.code === 200) {
          this.categories = [...incomeRes.data, ...expenseRes.data]
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    },
    
    // 提交表单
    async submitForm() {
      await this.$refs.recordFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const userId = JSON.parse(localStorage.getItem('user')).id
            let res
            
            if (this.isEditMode) {
              // 编辑记录
              res = await request.put(`/record/${this.recordForm.id}`, {
                ...this.recordForm,
                userId
              })
              
              if (res.code === 200) {
                this.$message.success('编辑记录成功')
                this.$emit('success', res.data)
                this.dialogVisible = false
                this.resetForm()
              } else {
                this.$message.error('编辑记录失败：' + (res.msg || '未知错误'))
              }
            } else {
              // 添加记录
              res = await request.post('/record', {
                ...this.recordForm,
                userId
              })
              
              if (res.code === 200) {
                this.$message.success('添加记录成功')
                this.$emit('success', res.data)
                this.dialogVisible = false
                this.resetForm()
              } else {
                this.$message.error('添加记录失败：' + (res.msg || '未知错误'))
              }
            }
          } catch (error) {
            console.error(this.isEditMode ? '编辑记录失败:' : '添加记录失败:', error)
            this.$message.error(this.isEditMode ? '编辑记录失败：网络错误' : '添加记录失败：网络错误')
          }
        }
      })
    },
    
    // 重置表单
    resetForm() {
      if (this.$refs.recordFormRef) {
        this.$refs.recordFormRef.resetFields()
      }
      this.recordForm = {
        id: '',
        type: '',
        amount: '',
        categoryId: '',
        recordTime: new Date(),
        remark: ''
      }
      this.isEditMode = false
    },
    
    // 打开添加对话框
    open() {
      this.isEditMode = false
      this.dialogVisible = true
      this.getCategories()
    },
    
    // 打开编辑对话框
    openEdit(record) {
      this.isEditMode = true
      // 填充表单数据
      this.recordForm = {
        id: record.id,
        type: record.type,
        amount: record.amount,
        categoryId: record.categoryId,
        recordTime: new Date(record.recordTime),
        remark: record.remark || ''
      }
      this.dialogVisible = true
      this.getCategories()
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
<template>
  <div>
    <!-- search bar -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.username" placeholder="Username" clearable></el-input>
          <el-input v-model="searchModel.phone" placeholder="Phone" clearable></el-input>
          <el-button @click="getUserList" type="primary" round icon="el-icon-search">Search</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button @click="openEditUI(null)" type="primary" icon="el-icon-plus" circle></el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- Results -->
    <el-card>
      <el-table :data="userList" stripe style="width: 100%">
        <el-table-column label="#" width="80">
          <template slot-scope="scope">
            <!-- (pageNo - 1) * pageSize + index + 1 -->
            {{(searchModel.pageNo-1) * searchModel.pageSize + scope.$index + 1}}
          </template>
        </el-table-column>
        <el-table-column prop="id" label="User ID" width="180">
        </el-table-column>
        <el-table-column prop="username" label="Username" width="180">
        </el-table-column>
        <el-table-column prop="phone" label="Phone" width="180">
        </el-table-column>
        <el-table-column prop="status" label="Status" width="180">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status == 1">normal</el-tag>
            <el-tag v-else type="danger">banned</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="Email">
        </el-table-column>
        <el-table-column label="Edit" width="180">
          <template slot-scope="scope">
            <el-button @click="openEditUI(scope.row.id)" type="primary" icon="el-icon-edit" size="mini" circle></el-button>
            <el-button @click="deleteUser(scope.row)" type="danger" icon="el-icon-delete" size="mini" circle></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Pagination -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <!-- user information edit box-->
    <el-dialog @close="clearForm" :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="userForm" ref="userFormRef" :rules="rules">
        <el-form-item label="Username" prop="username" :label-width="formLabelWidth">
          <el-input v-model="userForm.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="userForm.id == null || userForm.id == undefined" label="Password" prop="password" :label-width="formLabelWidth">
          <el-input type="password" v-model="userForm.password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Phone" prop="phone" :label-width="formLabelWidth">
          <el-input v-model="userForm.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email" :label-width="formLabelWidth">
          <el-input v-model="userForm.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Status" :label-width="formLabelWidth">
          <el-switch
            v-model="userForm.status"
            :active-value="1"
            :inactive-value="0"
            >
          </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Back</el-button>
        <el-button type="primary" @click="saveUser">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import userApi from '@/api/userManage'
export default {
  data(){
    var checkEmail = (rule, value, callback) => {
      var reg = /^[A-Za-z0-9]+([_\.][A-Za-z0-9]+)*@([A-Za-z0-9\-]+\.)+[A-Za-z]{2,6}$/
      if (!reg.test(value)) {
        return callback(new Error('Please enter a valid email'));
      }
      callback();
    };
    var checkPhone = (rule, value, callback) => {
      var reg = /^1{0,1} {0,1}(\d3|\d{3})-{0,1} {0,1}\d{3}-{0,1} {0,1}\d{4}$/
      if (!reg.test(value)) {
        return callback(new Error('Please enter a valid phone number'));
      }
      callback();
    }
    return {
      formLabelWidth: '130px',
      userForm: {},
      dialogFormVisible: false,
      title: "",
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      userList: [],
      rules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur'},
          { min: 3, max: 50, message: 'Username should be between 3 to 50 characters', trigger: 'blur'}
        ],
        password: [
          { required: true, message: 'Please enter default password', trigger: 'blur'},
          { min: 6, max: 18, message: 'Password should be between 6 to 18 characters', trigger: 'blur'}
        ],
        email: [
          { required: true, message: 'Please enter email', trigger: 'blur'},
          { validator: checkEmail, trigger: 'blur' }
        ],
        phone: [
          { required: true, message: 'Please enter a phone number', trigger: 'blur'},
          { validator: checkPhone, trigger: 'blur'}
        ]
      }
    }
  },
  methods:{
    deleteUser(user) {
      this.$confirm(`Confirm to delete ${user.username}`, 'Hint', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        userApi.deleteUserById(user.id).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          });
          this.getUserList();
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Cancelled'
        });
      });
    },
    saveUser() {
      //触发表单验证
      this.$refs.userFormRef.validate((valid) => {
        if (valid) {
          //提交请求给后台
          userApi.saveUser(this.userForm).then(response => {
            //success alert
            this.$message({
              message: response.message,
              type: 'success'
            });
            //close dialog box
            this.dialogFormVisible = false;
            //refersh form
            this.getUserList();
          });
        } else {
          console.log('error submit');
          return false;
        }
      });
    },
    clearForm() {
      this.userForm = {};
      this.$refs.userFormRef.clearValidate();
    },
    openEditUI(id) {
      if (id == null) {
        this.title = 'Add New User';
      } else {
        this.title = "Edit User";
        //search user info by id
        userApi.getUserById(id).then(response => {
          this.userForm = response.data;
        });
      }
      this.dialogFormVisible = true;
    },
    handleSizeChange(pageSize){
      this.searchModel.pageSize = pageSize;
      this.getUserList();
    },
    handleCurrentChange(pageNo){
      this.searchModel.pageNo = pageNo;
      this.getUserList();
    },
    getUserList(){
      userApi.getUserList(this.searchModel).then(response => {
        this.userList = response.data.rows;
        this.total = response.data.total;
      });
    }
  },
  //调用上面getuserlist which调用userManage里which调用axios
  created(){
    this.getUserList();
  }
};
</script>

<style>
#search .el-input {
  width: 200px;
  margin-right: 10px;
}
.el-dialog .el-input{
  width: 80%;
}
</style>
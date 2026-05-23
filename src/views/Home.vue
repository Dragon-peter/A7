<script setup>

import Header from "@/views/room/Header.vue";
import Aside from "@/views/room/Aside.vue";
</script>

<template>
  <el-container class="main-container">
    <el-header height="75px">
      <Header style="height: 75px;"/>
    </el-header>
    
    <el-container>
      <el-aside >
        <Aside />
      </el-aside>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.main-container {
  height: 100vh;
  background: var(--el-fill-color-light);
}

.el-header {
  padding: 0;
  width: 96vw !important;
}

.el-aside {
  position: static !important;
  height: auto;
  
  padding: 7px;
  background: var(--el-fill-color-light);
}

.el-main {
  padding: 20px;
  background: var(--el-fill-color-light);
}


</style>
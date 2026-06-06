import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/layout/Index.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('@/views/front/Home.vue') },
      { path: 'dogs', name: 'Dogs', component: () => import('@/views/front/Dogs.vue') },
      { path: 'dog/:id', name: 'DogDetail', component: () => import('@/views/front/DogDetail.vue') },
      { path: 'stories', name: 'Stories', component: () => import('@/views/front/Stories.vue') },
      { path: 'story/:id', name: 'StoryDetail', component: () => import('@/views/front/StoryDetail.vue') },
      { path: 'donate', name: 'Donate', component: () => import('@/views/front/Donate.vue') },
      { path: 'volunteer', name: 'Volunteer', component: () => import('@/views/front/Volunteer.vue') },
      { path: 'lost-found', name: 'LostFound', component: () => import('@/views/front/LostFound.vue') },
      { path: 'recognize', name: 'Recognize', component: () => import('@/views/front/Recognize.vue') },
      { path: 'adopt-apply/:id', name: 'AdoptApply', component: () => import('@/views/front/AdoptApply.vue') }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue')
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'dogs', name: 'AdminDogs', component: () => import('@/views/admin/Dogs.vue') },
      { path: 'adoptions', name: 'AdminAdoptions', component: () => import('@/views/admin/Adoptions.vue') },
      { path: 'donations', name: 'AdminDonations', component: () => import('@/views/admin/Donations.vue') },
      { path: 'stories', name: 'AdminStories', component: () => import('@/views/admin/Stories.vue') },
      { path: 'volunteers', name: 'AdminVolunteers', component: () => import('@/views/admin/Volunteers.vue') },
      { path: 'lost-found', name: 'AdminLostFound', component: () => import('@/views/admin/LostFound.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router

import type { GlobalThemeOverrides } from 'naive-ui'
import { computed, watch } from 'vue'
import { darkTheme, useOsTheme } from 'naive-ui'
import { useAppStore } from '@/store'

export function useTheme() {
  const appStore = useAppStore()

  const OsTheme = useOsTheme()

  const isDark = computed(() => {
    if (appStore.theme === 'auto')
      return OsTheme.value === 'dark'
    else
      return appStore.theme === 'dark'
  })

  const theme = computed(() => {
    return isDark.value ? darkTheme : undefined
  })

  const themeOverrides = computed<GlobalThemeOverrides>(() => {
    if (isDark.value) {
      return {
        // common: {
        //   primaryColor: '#fff',
        //   primaryColorHover: '#fff',
        //   primaryColorPressed: '#fff',
        //   textColor: '#fff',
        // },
        // Button: {
        //   textColor: '#fff',
        // },
      }
    }
    return {
      // common: {
      //   primaryColor: '#005bd4',
      //   primaryColorHover: '#005bd4',
      //   primaryColorPressed: '#005bd4',
      //   textColor: '#005bd4',
      // },
      // Button: {
      //   textColor: '#005bd4',
      // },
    }
  })

  watch(
    () => isDark.value,
    (dark) => {
      if (dark)
        document.documentElement.classList.add('dark')
      else
        document.documentElement.classList.remove('dark')
    },
    { immediate: true },
  )

  return { theme, themeOverrides }
}

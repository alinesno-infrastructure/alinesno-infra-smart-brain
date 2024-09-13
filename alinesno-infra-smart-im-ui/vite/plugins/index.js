import vue from '@vitejs/plugin-vue'

import createAutoImport from './auto-import'
import createSvgIcon from './svg-icon'
import createCompression from './compression'
import createSetupExtend from './setup-extend'

import { viteCommonjs } from '@originjs/vite-plugin-commonjs'

export default function createVitePlugins(viteEnv, isBuild = false) {
    const vitePlugins = [vue()]

    vitePlugins.push(viteCommonjs())

    vitePlugins.push(createAutoImport())
	vitePlugins.push(createSetupExtend())
    vitePlugins.push(createSvgIcon(isBuild))
	isBuild && vitePlugins.push(...createCompression(viteEnv))
    return vitePlugins
}

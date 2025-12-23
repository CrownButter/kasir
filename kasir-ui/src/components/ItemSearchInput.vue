<template>
  <div class="relative w-full">
    <input 
      type="text" 
      v-model="searchQuery"
      @input="onInput"
      @focus="showDropdown = true"
      @blur="delayHide"
      class="w-full outline-none bg-transparent font-bold placeholder-gray-400"
      placeholder="Ketik nama barang / jasa..."
    />

    <div v-if="showDropdown && filteredItems.length > 0" 
         class="absolute z-50 bg-white border border-blue-300 shadow-xl rounded mt-1 w-[150%] max-h-48 overflow-y-auto left-0">
      
      <div 
        v-for="item in filteredItems" 
        :key="item.id"
        @mousedown="selectItem(item)"
        class="p-2 hover:bg-blue-50 cursor-pointer border-b last:border-0 flex justify-between items-center text-sm"
      >
        <div>
            <div class="font-bold text-gray-800">{{ item.nama }}</div>
            <div class="text-xs text-gray-500">Kode: {{ item.kode }}</div>
        </div>
        <div class="text-right">
            <div class="font-bold text-blue-600">Rp {{ formatNumber(item.harga) }}</div>
            <div class="text-[10px]" :class="item.stok > 0 ? 'text-green-600' : 'text-red-500'">
                Stok: {{ item.stok }}
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import api from '../api';

const props = defineProps(['modelValue', 'items']);
const emit = defineEmits(['update:modelValue', 'select']);

const searchQuery = ref(props.modelValue || "");
const showDropdown = ref(false);
let timeoutId = null;

watch(() => props.modelValue, (val) => {
    searchQuery.value = val;
});

const filteredItems = computed(() => {
    if(!searchQuery.value || !props.items) return [];
    const lower = searchQuery.value.toLowerCase();
    // Filter barang yang stoknya > 0 atau sesuaikan kebutuhan
    return props.items.filter(i => 
        (i.nama.toLowerCase().includes(lower) || (i.kode && i.kode.toLowerCase().includes(lower)))
    );
});

const onInput = () => {
    emit('update:modelValue', searchQuery.value); 
    showDropdown.value = true;
};

const selectItem = (item) => {
    if (item.stok <= 0) {
        alert("Stok Habis!");
        return;
    }
    searchQuery.value = item.nama;
    emit('update:modelValue', item.nama);
    emit('select', item); 
    showDropdown.value = false;
};

const delayHide = () => {
    setTimeout(() => { showDropdown.value = false; }, 200);
};
const showOptions = () => {
  if (timeoutId) clearTimeout(timeoutId); // Batalkan timer jika user kembali fokus
  showDropdown.value = true;
};
onUnmounted(() => {
  if (timeoutId) {
    clearTimeout(timeoutId);
  }
});

const formatNumber = (n) => new Intl.NumberFormat('id-ID').format(n);
</script>
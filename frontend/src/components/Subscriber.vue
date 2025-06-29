<template>
    <div>
        <String
            label="구독자이름"
            v-model="value.name"
            :editMode="editMode"
        />
        <Date
            label="월 구독권 구매 여부"
            v-model="value.isMonthlySubscribed"
            :editMode="editMode"
        />
        <String
            label="IsKt"
            v-model="value.isKt"
            :editMode="editMode"
        />
        <v-row class="ma-0 pa-0">
            <v-spacer></v-spacer>
            <v-btn width="64px" color="primary" @click="save">
                저장
            </v-btn>
        </v-row>
    </div>
</template>


<script>
import BaseEntity from './base-ui/BaseEntity.vue'

export default {
    name: 'Subscriber',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "subscribers",
        value: {
        }
    }),
    created(){
    },
    computed:{
    },
    methods: {
    },
}
</script>

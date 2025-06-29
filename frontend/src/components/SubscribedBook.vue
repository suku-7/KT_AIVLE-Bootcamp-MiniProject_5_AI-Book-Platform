<template>
    <div>
        <Number
            label="SubscribedBookId"
            v-model="value.subscribedBookId"
            :editMode="editMode"
        />
        <Boolean
            label="Status"
            v-model="value.status"
            :editMode="editMode"
        />
        <Number
            label="BookId"
            v-model="value.bookId"
            :editMode="editMode"
        />
        <SubscriberId
            offline
            label="구독자 ID"
            v-model="value.subscriberId"
            :editMode="editMode"
            @change="change"
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
    name: 'SubscribedBook',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "subscribedBooks",
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

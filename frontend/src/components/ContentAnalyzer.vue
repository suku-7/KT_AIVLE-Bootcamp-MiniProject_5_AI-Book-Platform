<template>
    <div>
        <String
            label="BookId"
            v-model="value.bookId"
            :editMode="editMode"
        />
        <String
            label="Language"
            v-model="value.language"
            :editMode="editMode"
        />
        <Number
            label="MaxLength"
            v-model="value.maxLength"
            :editMode="editMode"
        />
        <String
            label="ClassificationType"
            v-model="value.classificationType"
            :editMode="editMode"
        />
        <String
            label="RequestedBy"
            v-model="value.requestedBy"
            :editMode="editMode"
        />
        <Text
            offline
            label="Content"
            v-model="value.content"
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
    name: 'ContentAnalyzer',
    mixins:[BaseEntity],
    components:{
    },
    
    data: () => ({
        path: "contentAnalyzers",
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

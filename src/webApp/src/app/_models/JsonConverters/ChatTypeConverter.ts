import {JsonConverter, JsonCustomConvert} from 'json2typescript';
import {ChatType} from '../ChatType';

@JsonConverter
export class ChatTypeConverter implements JsonCustomConvert<ChatType> {
    serialize(type: ChatType): string {
        return type;
    }
    deserialize(type: string): ChatType {
        return type === ChatType.PRIVATE ? ChatType.PRIVATE : ChatType.GROUP;
    }
}

package io.github.ilopezluna.gguf4j.core;

/**
 * Constants used in the GGUF file format.
 */
public final class GGUFConstants {
    
    // GGUF magic number
    public static final int GGUF_MAGIC = 0x46554747; // "GGUF" in little-endian
    
    // GGUF versions
    public static final int GGUF_VERSION_1 = 1;
    public static final int GGUF_VERSION_2 = 2;
    public static final int GGUF_VERSION_3 = 3;
    
    // Default alignment for tensor data
    public static final int DEFAULT_ALIGNMENT = 32;
    
    // Common metadata keys
    public static final String GENERAL_ARCHITECTURE = "general.architecture";
    public static final String GENERAL_NAME = "general.name";
    public static final String GENERAL_AUTHOR = "general.author";
    public static final String GENERAL_VERSION = "general.version";
    public static final String GENERAL_DESCRIPTION = "general.description";
    public static final String GENERAL_LICENSE = "general.license";
    public static final String GENERAL_SOURCE_URL = "general.source.url";
    public static final String GENERAL_SOURCE_HF_REPO = "general.source.huggingface.repository";
    public static final String GENERAL_FILE_TYPE = "general.file_type";
    public static final String GENERAL_QUANTIZATION_VERSION = "general.quantization_version";
    
    // Architecture-specific metadata keys
    public static final String CONTEXT_LENGTH = ".context_length";
    public static final String EMBEDDING_LENGTH = ".embedding_length";
    public static final String BLOCK_COUNT = ".block_count";
    public static final String FEED_FORWARD_LENGTH = ".feed_forward_length";
    public static final String ATTENTION_HEAD_COUNT = ".attention.head_count";
    public static final String ATTENTION_HEAD_COUNT_KV = ".attention.head_count_kv";
    public static final String ATTENTION_LAYER_NORM_RMS_EPSILON = ".attention.layer_norm_rms_epsilon";
    public static final String ROPE_DIMENSION_COUNT = ".rope.dimension_count";
    public static final String ROPE_FREQ_BASE = ".rope.freq_base";
    public static final String ROPE_SCALING_TYPE = ".rope.scaling.type";
    public static final String ROPE_SCALING_FACTOR = ".rope.scaling.factor";
    
    // Tokenizer metadata keys
    public static final String TOKENIZER_MODEL = "tokenizer.model";
    public static final String TOKENIZER_LIST = "tokenizer.list";
    public static final String TOKENIZER_TOKEN_TYPE = "tokenizer.token_type";
    public static final String TOKENIZER_SCORES = "tokenizer.scores";
    public static final String TOKENIZER_MERGES = "tokenizer.merges";
    public static final String TOKENIZER_BOS_TOKEN_ID = "tokenizer.bos_token_id";
    public static final String TOKENIZER_EOS_TOKEN_ID = "tokenizer.eos_token_id";
    public static final String TOKENIZER_UNK_TOKEN_ID = "tokenizer.unk_token_id";
    public static final String TOKENIZER_SEP_TOKEN_ID = "tokenizer.sep_token_id";
    public static final String TOKENIZER_PAD_TOKEN_ID = "tokenizer.pad_token_id";
    public static final String TOKENIZER_ADD_BOS_TOKEN = "tokenizer.add_bos_token";
    public static final String TOKENIZER_ADD_EOS_TOKEN = "tokenizer.add_eos_token";
    public static final String TOKENIZER_HF_JSON = "tokenizer.huggingface.json";
    
    // Common architectures
    public static final String ARCH_LLAMA = "llama";
    public static final String ARCH_FALCON = "falcon";
    public static final String ARCH_BAICHUAN = "baichuan";
    public static final String ARCH_GPT2 = "gpt2";
    public static final String ARCH_GPTJ = "gptj";
    public static final String ARCH_GPTNEOX = "gptneox";
    public static final String ARCH_MPT = "mpt";
    public static final String ARCH_STARCODER = "starcoder";
    public static final String ARCH_PERSIMMON = "persimmon";
    public static final String ARCH_REFACT = "refact";
    public static final String ARCH_BERT = "bert";
    public static final String ARCH_NOMIC_BERT = "nomic-bert";
    public static final String ARCH_BLOOM = "bloom";
    public static final String ARCH_STABLELM = "stablelm";
    public static final String ARCH_QWEN = "qwen";
    public static final String ARCH_QWEN2 = "qwen2";
    public static final String ARCH_PHI2 = "phi2";
    public static final String ARCH_PHI3 = "phi3";
    public static final String ARCH_PLAMO = "plamo";
    public static final String ARCH_CODESHELL = "codeshell";
    public static final String ARCH_ORION = "orion";
    public static final String ARCH_INTERNLM2 = "internlm2";
    public static final String ARCH_MINICPM = "minicpm";
    public static final String ARCH_GEMMA = "gemma";
    public static final String ARCH_STARCODER2 = "starcoder2";
    public static final String ARCH_MAMBA = "mamba";
    public static final String ARCH_XVERSE = "xverse";
    public static final String ARCH_COMMAND_R = "command-r";
    public static final String ARCH_DBRX = "dbrx";
    public static final String ARCH_OLMO = "olmo";
    public static final String ARCH_OPENELM = "openelm";
    public static final String ARCH_ARCTIC = "arctic";
    public static final String ARCH_DEEPSEEK2 = "deepseek2";
    public static final String ARCH_CHATGLM = "chatglm";
    public static final String ARCH_BITNET = "bitnet";
    public static final String ARCH_T5 = "t5";
    public static final String ARCH_JAIS = "jais";
    public static final String ARCH_NEMOTRON = "nemotron";
    public static final String ARCH_EXAONE = "exaone";
    
    // File type constants (quantization types)
    public static final int FILE_TYPE_ALL_F32 = 0;
    public static final int FILE_TYPE_MOSTLY_F16 = 1;
    public static final int FILE_TYPE_MOSTLY_Q4_0 = 2;
    public static final int FILE_TYPE_MOSTLY_Q4_1 = 3;
    public static final int FILE_TYPE_MOSTLY_Q4_1_SOME_F16 = 4;
    public static final int FILE_TYPE_MOSTLY_Q8_0 = 7;
    public static final int FILE_TYPE_MOSTLY_Q5_0 = 8;
    public static final int FILE_TYPE_MOSTLY_Q5_1 = 9;
    public static final int FILE_TYPE_MOSTLY_Q2_K = 10;
    public static final int FILE_TYPE_MOSTLY_Q3_K_S = 11;
    public static final int FILE_TYPE_MOSTLY_Q3_K_M = 12;
    public static final int FILE_TYPE_MOSTLY_Q3_K_L = 13;
    public static final int FILE_TYPE_MOSTLY_Q4_K_S = 14;
    public static final int FILE_TYPE_MOSTLY_Q4_K_M = 15;
    public static final int FILE_TYPE_MOSTLY_Q5_K_S = 16;
    public static final int FILE_TYPE_MOSTLY_Q5_K_M = 17;
    public static final int FILE_TYPE_MOSTLY_Q6_K = 18;
    public static final int FILE_TYPE_MOSTLY_IQ2_XXS = 19;
    public static final int FILE_TYPE_MOSTLY_IQ2_XS = 20;
    public static final int FILE_TYPE_MOSTLY_Q2_K_S = 21;
    public static final int FILE_TYPE_MOSTLY_IQ3_XS = 22;
    public static final int FILE_TYPE_MOSTLY_IQ3_XXS = 23;
    public static final int FILE_TYPE_MOSTLY_IQ1_S = 24;
    public static final int FILE_TYPE_MOSTLY_IQ4_NL = 25;
    public static final int FILE_TYPE_MOSTLY_IQ3_S = 26;
    public static final int FILE_TYPE_MOSTLY_IQ2_S = 27;
    public static final int FILE_TYPE_MOSTLY_IQ4_XS = 28;
    public static final int FILE_TYPE_MOSTLY_IQ1_M = 29;
    public static final int FILE_TYPE_MOSTLY_BF16 = 30;
    public static final int FILE_TYPE_MOSTLY_Q4_0_4_4 = 31;
    public static final int FILE_TYPE_MOSTLY_Q4_0_4_8 = 32;
    public static final int FILE_TYPE_MOSTLY_Q4_0_8_8 = 33;
    
    private GGUFConstants() {
        // Utility class
    }
}

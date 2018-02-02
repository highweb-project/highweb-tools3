//**************************************
// Name: Lexical Analyzer in C
// Description:It will lexically Analyze the given file(C program) and it willgive the various tokens present in it..
// By: Aditya Siddharth Dutt (from psc cd)
//
// Inputs:Input the Complete file name with pateh... It can also be given in the program itsel.. i have commented the section...
//
// Returns:keywords,identifiers,operators,headers,arguments,constants
//
// Side Effects:Be sure the path is correct
//**************************************

#include<stdio.h>
#include<stdlib.h>
#include<ctype.h>
#include<string.h>

#define MAX_TOKEN_LENGTH 32
#define NAME_ROOT "root"

struct Token;

void keyw(char *p);
void add_token(const char*, const char*);
void print_all_tokens();
void free_token(struct Token *);
void free_tokens();

int i = 0;
int id = 0;
int kernelkw = 0;
int kw = 0;
int num = 0;
int op = 0;
char keys[19][MAX_TOKEN_LENGTH] = { "auto", "break", "case", "continue", "default", "do",
"else", "enum", "for", "goto", "if", "register", "return", "sizeof",
"struct", "switch", "typedef", "union", "while" };
char kernelkeys[16][MAX_TOKEN_LENGTH] = { "__global", "global", "__local", "local",
"__constant", "constant", "__private", "private", "__kernel", "kernel",
"__read_only", "read_only", "__write_only", "write_only",
"__read_write", "read_write" };
char typekeys[64][MAX_TOKEN_LENGTH] = { "char", "const", "double", "extern", "float", "int",
"long", "short", "signed", "static", "unsigned", "void", "volatile",
"bool", "uchar", "ushort", "uint", "ulong", "half", "size_t",
"char2", "char4", "char8", "char16", "uchar2", "uchar4", "uchar8", "uchar16",
"short2", "short4", "short8", "short16", "ushort2", "ushort4", "ushort8", "ushort16",
"int2", "int4", "int8", "int16", "uint2", "uint4", "uint8", "uint16",
"long2", "long4", "long8", "long16", "ulong2", "ulong4", "ulong8", "ulong16",
"float2", "float4", "float8", "float16", "double2", "double4", "double8", "double16",
"half2", "half4", "half8", "half16"
};
FILE *outfile;
int isOutputFile = 0;
int isVerbose = 0;

struct Token {
	struct Token* prev;
	char* name;
	char identification[64];
	int offset;
	struct Token* next;
};
struct Token* tokens;
struct Token* first;
int offset;

//

typedef struct SN {
	char data;
	struct SN *link;
}stackN;

stackN *top = NULL;

void push(char item) {
	stackN * new = (stackN *)malloc(sizeof(stackN));
	new->data = item;
	new->link = top;
	top = new;
}
char pop() {
	char item;
	stackN * old = top;
	if (top) {
		item = old->data;
		top = old->link;
		free(old);
		return item;
	}
	printf("\n\n Stack is EMPTY ! \n");
}

struct Token* function_token;

void main(int argc, char *argv[]) {
	char ch;
	char str[25];
	char seps[15] = " \t\n,;(){}[]#\"<>";
	char oper[] = "!%^&*-+=~|.<>/?";
	int j;
	char fname[50];
	FILE *f1;
	//clrscr();
	//printf("Argument count : %d\n", argc);
	if (argc == 1) {
		printf("Usage : cparser input_file_name (-f output_file_name)\n");
		return;
	}
	//scanf("%s",fname);
	sprintf(fname, "%s\0", argv[1]);
	f1 = fopen(fname, "r");
	//f1 = fopen("Input","r");
	if (f1 == NULL) {
		printf("file not found");
		exit(0);
	}
	if (argc == 2) {
		isVerbose = 1;
	}
	else {
		int i;
		for (i = 2; i < argc; i++) {
			if (!strcmp(argv[i], "-v")) {
				isVerbose = 1;
			}
			else if (!strcmp(argv[i], "-f")) {
				if ((!isVerbose && argc == 3) || (isVerbose && argc == 4)) {
					printf("Please give the value of -f\n");
					exit(1);
				}
				i++;
				sprintf(fname, "%s\0", argv[i]);
				outfile = fopen(fname, "w");
				if (outfile != NULL) {
					isOutputFile = 1;
				}
			}
		}
	}
	tokens = (struct Token*) malloc(sizeof(struct Token));
	tokens->prev = NULL;
	tokens->next = NULL;
	tokens->name = (char*)malloc(sizeof(char) * (MAX_TOKEN_LENGTH + 1));
	tokens->offset = -1;
	sprintf(tokens->name, "%s\0", NAME_ROOT);
	sprintf(tokens->identification, "%s\0", NAME_ROOT);

	first = tokens;
	offset = -1;
	while ((ch = fgetc(f1)) != EOF) {
		offset++;
		for (j = 0; j <= 14; j++) {
			if (ch == oper[j]) {
				char tmp[2];
				sprintf(tmp, "%c\0", ch);
				add_token(tmp, "operator");
				//printf("%c:operator\n",ch);
				//if (isOutputFile) {
				//	fprintf(outfile, "%c:operator\n", ch);
				//}

				op++;
				str[i] = '\0';
				keyw(str);
				if (tokens->prev != NULL && tokens->prev->prev != NULL) {
					if (!strcmp(tokens->prev->identification, "identifier")) {
						if (!strcmp(tokens->prev->prev->identification, "type_keyword")) {
							sprintf(tokens->prev->identification, "variable_declaration\0");
						}
						else if (tokens->prev->prev->prev != NULL) {
							if (!strcmp(tokens->prev->prev->identification, "operator")
								&& !strcmp(tokens->prev->prev->prev->identification, "type_keyword")) {
								sprintf(tokens->prev->identification, "variable_declaration\0");
							}
						}
					}
				}
			}
		}
		for (j = 0; j <= 14; j++) {
			if (i == -1)
				break;
			if (ch == seps[j]) {
				char wds[128];
				int m;
				if (ch == '#') {
					m = 0;
					while (ch != '>') {
						wds[m++] = ch;
						//printf("%c",ch);
						//if (isOutputFile) {
						//	fprintf(outfile, "%c", ch);
						//}
						ch = fgetc(f1);
						offset++;
					}
					wds[m++] = ch;
					wds[m] = '\0';
					add_token(wds, "header_file");
					//printf("%c:header_file\n",ch);
					//if (isOutputFile) {
					//	fprintf(outfile, "%c:header_file\n", ch);
					//}
					i = -1;
					break;
				}
				if (ch == '"') {
					m = 0;
					do {
						ch = fgetc(f1);
						offset++;
						wds[m++] = ch;
						//printf("%c",ch);
						//if (isOutputFile) {
						//	fprintf(outfile, "%c", ch);
						//}
					} while (ch != '"');
					wds[m] = '\0';
					add_token(wds, "argument");
					//printf(":argument\n");
					//if (isOutputFile) {
					//	fprintf(outfile, ":argument\n");
					//}
					i = -1;
					break;
				}
				str[i] = '\0';
				keyw(str);
				if (ch == '(') {
					if (tokens->prev != NULL) {
						if (!strcmp(tokens->identification, "identifier")) {
							if (!strcmp(tokens->prev->identification, "type_keyword")) {
								sprintf(tokens->identification, "function_declaration");
								function_token = tokens;
							}
							else if (tokens->prev->prev != NULL) {
								if (!strcmp(tokens->prev->identification, "operator")
									&& !strcmp(tokens->prev->prev->identification, "type_keyword")) {
									sprintf(tokens->identification, "function_declaration");
									function_token = tokens;
								}
							}
						}
					}
				}
				else if (ch == '{') {
					if (function_token != NULL) {
						if (top) {

						}
						else {
							sprintf(function_token->identification, "%s/%d", function_token->identification, (offset - 1));
						}
						push(ch);

					}
				}
				else if (ch == '}') {
					if (function_token != NULL) {
						pop();
						if (top) {

						}
						else {
							sprintf(function_token->identification, "%s,%d\0", function_token->identification, (offset - 1));
						}

					}
				}
				else if (ch == ';' || ch == '[') {
					if (tokens->prev != NULL) {
						if (!strcmp(tokens->identification, "identifier")) {
							if (!strcmp(tokens->prev->identification, "type_keyword")) {
								sprintf(tokens->identification, "variable_declaration\0");
							}
							else if (tokens->prev->prev != NULL) {
								if (!strcmp(tokens->prev->identification, "operator")
									&& !strcmp(tokens->prev->prev->identification, "type_keyword")) {
									sprintf(tokens->identification, "variable_declaration\0");
								}
							}
						}
					}
				}
				else if (ch == '\n') {
					strcat(tokens->name, "_newline");
				}
			}
		}
		if (i != -1) {
			str[i] = ch;
			i++;
		}
		else
			i = 0;
	}
	print_all_tokens();
	free_tokens();
	//printf(
	//		"\n\nKeywords: %d\nKernel Keywords: %d\nIdentifiers: %d\nOperators: %d\nNumbers: %d\n",
	//		kw, kernelkw, id, op, num);
	if (isOutputFile) {
		//	fprintf(outfile,
		//			"\n\nKeywords: %d\nKernel Keywords: %d\nIdentifiers: %d\nOperators: %d\nNumbers: %d\n",
		//			kw, kernelkw, id, op, num);
		fclose(outfile);
	}
	fclose(f1);
	//getch();
}

void print_all_tokens() {
	struct Token* tok = first;
	tok = tok->next;
	while (tok->next != NULL) {
		if (isVerbose) {
			printf("%s:%s:%d\n", tok->name, tok->identification, tok->offset);
		}
		if (isOutputFile) {
			fprintf(outfile, "%s:%s:%d\n", tok->name, tok->identification,
				tok->offset);
		}
		tok = tok->next;
	}
}

void keyw(char *p) {
	int k, flag = 0;
	for (k = 0; k <= 18; k++) {
		if (strcmp(keys[k], p) == 0) {
			add_token(p, "keyword");
			//printf("%s:keyword\n", p);
			//if (isOutputFile) {
			//	fprintf(outfile, "%s:keyword\n", p);
			//}
			kw++;
			flag = 1;
			break;
		}
	}
	for (k = 0; k <= 15; k++) {
		if (strcmp(kernelkeys[k], p) == 0) {
			add_token(p, "kernel_keyword");
			//printf("%s:kernel_keyword\n", p);
			//if (isOutputFile) {
			//	fprintf(outfile, "%s:kernel_keyword\n", p);
			//}
			kernelkw++;
			flag = 1;
			break;
		}
	}
	for (k = 0; k <= 63; k++) {
		if (strcmp(typekeys[k], p) == 0) {
			add_token(p, "type_keyword");
			//printf("%s:kernel_keyword\n", p);
			//if (isOutputFile) {
			//	fprintf(outfile, "%s:kernel_keyword\n", p);
			//}
			kernelkw++;
			flag = 1;
			break;
		}
	}
	if (flag == 0) {
		if (isdigit(p[0])) {
			add_token(p, "number");
			//printf("%s:number\n", p);
			//if (isOutputFile) {
			//	fprintf(outfile, "%s:number\n", p);
			//}
			num++;
		}
		else {
			//if(p[0]!=13&&p[0]!=10)
			if (p[0] != '\0') {
				add_token(p, "identifier");
				//printf("%s:identifier\n", p);
				//if (isOutputFile) {
				//	fprintf(outfile, "%s:identifier\n", p);
				//}
				id++;
			}
		}
	}
	i = -1;
}

void add_token(const char* name, const char* indentification) {
	struct Token *tok;
	int namelen = strlen(name);

	tok = (struct Token*) malloc(sizeof(struct Token));
	tok->name = (char*)malloc(sizeof(char) * (MAX_TOKEN_LENGTH + 1));
	sprintf(tok->name, "%s\0", name);
	sprintf(tok->identification, "%s\0", indentification);
	if (offset >= namelen) {
		tok->offset = offset - namelen;
	}
	else {
		tok->offset = -1;
	}
	tok->prev = tokens;
	tok->next = NULL;
	tokens->next = tok;
	tokens = tok;
}

void free_token(struct Token *token) {
	if (token != NULL) {
		if (token->name != NULL) {
			free(token->name);
			token->name = NULL;
		}
		free(token);
		token = NULL;
	}
}

void free_tokens() {
	struct Token *tok;
	tokens = first;
	while (tokens != NULL) {
		tok = tokens->next;
		free_token(tokens);
		tokens = tok;
	}
}

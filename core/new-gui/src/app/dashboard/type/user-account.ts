
export interface UserAccount extends Readonly<{
  userName: string;
  userID: number;
}> {}

export interface UserAccountResponse extends Readonly<{
  code: 0 | 1; // 0 represents success and 1 represents error
  userAccount: UserAccount;
  message: string;
}> {}

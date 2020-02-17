/**
 * This interface stores the information about the users' files.
 * These information is used to locate the file for the operators.
 */
export interface UserFile extends Readonly<{
  id: number;
  name: string;
  path: string;
  description: string;
}> {}

package org.kku.wandelpool.domain;

public class WandelpoolException
    extends Exception
{
  private static final long serialVersionUID = 1L;

  public WandelpoolException(Exception ex)
  {
    super(ex);
  }
}

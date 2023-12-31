"""Add post image url field to Noti

Revision ID: 1548536c4072
Revises: ff467ac4a64d
Create Date: 2023-11-27 18:24:57.711699

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '1548536c4072'
down_revision: Union[str, None] = 'ff467ac4a64d'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('notification', sa.Column('post_image_url', sa.String(), nullable=True))
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('notification', 'post_image_url')
    # ### end Alembic commands ###
